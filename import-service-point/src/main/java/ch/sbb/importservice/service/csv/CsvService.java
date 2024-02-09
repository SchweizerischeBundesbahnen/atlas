package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.DidokCsvMapper;
import ch.sbb.importservice.exception.CsvException;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CsvService<T> {

  private static final String HASHTAG = "#";
  private static final String CSV_DELIMITER = ";";
  protected static final String EDITED_AT_COLUMN_NAME_SERVICE_POINT = "GEAENDERT_AM";
  protected static final String EDITED_AT_COLUMN_NAME_PRM = "MODIFIED_DATE";

  private final FileHelperService fileHelperService;
  private final JobHelperService jobHelperService;

  protected CsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    this.fileHelperService = fileHelperService;
    this.jobHelperService = jobHelperService;
  }

  protected abstract CsvFileNameModel csvFileNameModel();

  protected abstract String getModifiedDateHeader();

  protected abstract String getImportCsvJobName();

  protected abstract Class<T> getType();

  public List<T> getActualCsvModelsFromS3() {
    log.info("Downloading file from Amazon S3 Bucket: {}", AmazonBucket.EXPORT);
    final File importFile = fileHelperService.downloadImportFileFromS3(csvFileNameModel());
    final LocalDate matchingDate = jobHelperService.getDateForImportFileToDownload(getImportCsvJobName());
    log.info("CSV File to import: {}", importFile.getName());
    final List<T> csvModels = getCsvModelsToUpdate(importFile, matchingDate);
    log.info("Found {} Csv Models", csvModels.size());
    fileHelperService.deleteConsumedFile(importFile);
    return csvModels;
  }

  public List<T> getActualCsvModels(File file) {
    log.info("Starting file import process");
    log.info("CSV File to import: {}", file.getName());
    final List<T> csvModels = getCsvModelsToUpdate(file, MIN_LOCAL_DATE);
    log.info("Found {} Csv Models to send to ServicePointDirectory", csvModels.size());
    return csvModels;
  }

  public List<T> getCsvModelsToUpdate(File importFile, LocalDate matchingDate) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile))) {
      String headerLine = skipUntilHeaderLine(bufferedReader);
      int numberOfAttributes = headerLine.split(CSV_DELIMITER).length;
      int editedAtColumnIndex = getColumnIndexOfEditedAt(headerLine);
      List<String> mismatchedLines = getMismatchedLines(matchingDate, bufferedReader, editedAtColumnIndex);
      log.info("Found {} lines to update", mismatchedLines.size());
      // parse mismatched lines
      if (mismatchedLines.isEmpty()) {
        return Collections.emptyList();
      }

      Pattern pattern = Pattern.compile("\\$newline\\$");
      mismatchedLines = mismatchedLines.stream().map((line) -> {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
          String[] attributes = line.split(CSV_DELIMITER);
          if (attributes.length == numberOfAttributes) {
            String[] newLine = matcher.replaceAll("\r\n").split(CSV_DELIMITER);
            newLine[editedAtColumnIndex] =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
            return String.join(CSV_DELIMITER, newLine);
          } else {
            log.error("Could not replace newline because numberOfAttributes is not correct for line: {}", line);
            return line;
          }
        }
        return line;
      }).toList();

      List<String> csvLinesToProcess = new ArrayList<>();
      csvLinesToProcess.add(headerLine);
      csvLinesToProcess.addAll(mismatchedLines);
      MappingIterator<T> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(getType())
          .with(DidokCsvMapper.CSV_SCHEMA)
          .readValues(String.join("\n", csvLinesToProcess));
      return mapObjects(mappingIterator);
    } catch (IOException e) {
      throw new CsvException(e);
    }
  }

  private String skipUntilHeaderLine(BufferedReader bufferedReader) throws IOException {
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (!line.contains(HASHTAG)) {
        return line;
      }
    }
    throw new CsvException("Not found header line in csv");
  }

  private int getColumnIndexOfEditedAt(String headerLine) {
    String[] attributes = headerLine.split(CSV_DELIMITER);
    int editedAtColumnIndex = Arrays.asList(attributes).indexOf(getModifiedDateHeader());
    if (editedAtColumnIndex == -1) {
      throw new CsvException("Not found %s index".formatted(getModifiedDateHeader()));
    }
    return editedAtColumnIndex;
  }

  private List<String> getMismatchedLines(LocalDate matchingDate, BufferedReader bufferedReader, int editedAtColumnIndex)
      throws IOException {
    List<String> mismatchedLines = new ArrayList<>();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      String[] splittedLine = line.split(CSV_DELIMITER);

      DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder().append(DateTimeFormatter.ofPattern(
          "[" + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN + "][" + AtlasApiConstants.DATE_FORMAT_PATTERN + "]"));
      DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
      try {
        LocalDate lastEditionDate = LocalDate.parse(splittedLine[editedAtColumnIndex], dateTimeFormatter);
        boolean dateMatchedBetweenTodayAndMatchingDate = jobHelperService.isDateMatchedBetweenTodayAndMatchingDate(matchingDate,
            lastEditionDate);
        if (dateMatchedBetweenTodayAndMatchingDate) {
          mismatchedLines.add(line);
        }
      } catch (DateTimeParseException e) {
        log.error("Could not parse date, will ignore this line for mismatchedLines, line: " + line, e);
      }

    }
    return mismatchedLines;
  }

  private List<T> mapObjects(MappingIterator<T> mappingIterator) {
    final List<T> mappedObjects = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      mappedObjects.add(mappingIterator.next());
    }
    return mappedObjects;
  }

}
