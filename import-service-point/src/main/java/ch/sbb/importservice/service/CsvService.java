package ch.sbb.importservice.service;

import ch.sbb.atlas.base.service.amazon.service.AmazonService;
import ch.sbb.atlas.base.service.imports.DidokCsvMapper;
import ch.sbb.atlas.base.service.imports.ServicePointCsvModel;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {

  private static final String HASHTAG = "#";
  private static final String SERVICEPOINT_DIDOK_DIR_NAME = "servicepoint_didok";
  private static final String CSV_DELIMITER = ";";

  private final AmazonService amazonService;

  @Setter
  private LocalDate matchingDate = LocalDate.now();

  //@PostConstruct
  public List<ServicePointCsvModel> getActualServicePotinCsvModelsFromS3() throws IOException {
    File importFile = downloadImportFileFromToday("DIDOK3_DIENSTSTELLEN_ALL_V_3_");
    List<ServicePointCsvModel> servicePointCsvModels = getCsvModelsToUpdate(importFile, ServicePointCsvModel.class);
    log.info("servicePointCsvModels size: {}", servicePointCsvModels.size());
    return servicePointCsvModels;
  }

  //@PostConstruct
  public List<ServicePointCsvModel> getActualServicePotinCsvModelsFromS3(File file) throws IOException {
    List<ServicePointCsvModel> servicePointCsvModels = getCsvModelsToUpdate(file, ServicePointCsvModel.class);
    log.info("servicePointCsvModels size: {}", servicePointCsvModels.size());
    return servicePointCsvModels;
  }

  // line by line comparison
  /*public void loadCsvFilesForImport() throws IOException {
    for (String csvImportFilePrefix : csvImportFilePrefixes) {
      String csvImportFilePrefixToday = attachTodayDate(csvImportFilePrefix);
      File importFileToday = downloadImportFileWithPrefix(csvImportFilePrefixToday);
      String csvImportFilePrefixYesterday = attachYesterdayDate(csvImportFilePrefix);
      File importFileYesterday = downloadImportFileWithPrefix(csvImportFilePrefixYesterday);
      long mismatch = Files.mismatch(importFileToday.toPath(), importFileYesterday.toPath());
      log.info("Mismatch: " + mismatch);
      if (mismatch != -1) {
        try (BufferedReader bufferedReaderFileToday = new BufferedReader(new FileReader(importFileToday));
            BufferedReader bufferedReaderFileYesterday = new BufferedReader(new FileReader(importFileYesterday))) {
          List<Integer> mismatchedLineNumbers = new ArrayList<>();
          String lineFileToday, lineFileYesterday;
          int lineNumber = 1;
          while ((lineFileYesterday = bufferedReaderFileYesterday.readLine()) != null) {
            lineFileToday = bufferedReaderFileToday.readLine();
            if (lineFileToday == null || !lineFileToday.equals(lineFileYesterday)) {
              mismatchedLineNumbers.add(lineNumber);
            }
            lineNumber++;
          }
          log.info("Line numbers: " + mismatchedLineNumbers);
        }
      }
    }
  }*/

  public File downloadImportFileFromToday(String csvImportFilePrefix) throws IOException {
    String csvImportFilePrefixToday = attachTodayDate(csvImportFilePrefix);
    return downloadImportFileWithPrefix(csvImportFilePrefixToday);
  }

  public <T> List<T> getCsvModelsToUpdate(File importFile, Class<T> type) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile))) {
      String headerLine = skipUntilHeaderLine(bufferedReader);
      int editedAtColumnIndex = getColumnIndexOfEditedAt(headerLine);
      List<String> mismatchedLines = getMismatchedLines(bufferedReader, editedAtColumnIndex);
      log.info("Mismatched lines count: " + mismatchedLines.size());
      // parse mismatched lines
      if (mismatchedLines.isEmpty()) {
        return Collections.emptyList();
      }
      List<String> csvLinesToProcess = new ArrayList<>();
      csvLinesToProcess.add(headerLine);
      csvLinesToProcess.addAll(mismatchedLines);
      MappingIterator<T> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(type)
          .with(DidokCsvMapper.CSV_SCHEMA)
          .readValues(String.join("\n", csvLinesToProcess));
      return mapObjects(mappingIterator);
    }
  }

  private String skipUntilHeaderLine(BufferedReader bufferedReader) throws IOException {
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (!line.contains(HASHTAG)) {
        return line;
      }
    }
    throw new RuntimeException("Not found header line in csv");
  }

  private int getColumnIndexOfEditedAt(String headerLine) {
    final String EDITED_AT_COLUMN_NAME = "GEAENDERT_AM";
    String[] attributes = headerLine.split(CSV_DELIMITER);
    int editedAtColumnIndex = Arrays.asList(attributes).indexOf(EDITED_AT_COLUMN_NAME);
    if (editedAtColumnIndex == -1) {
      throw new RuntimeException("Not found %s index".formatted(EDITED_AT_COLUMN_NAME));
    }
    return editedAtColumnIndex;
  }

  private List<String> getMismatchedLines(BufferedReader bufferedReader, int editedAtColumnIndex) throws IOException {
    List<String> mismatchedLines = new ArrayList<>();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      String[] splittedLine = line.split(CSV_DELIMITER);
      LocalDate lastEditionDate = LocalDateTime.parse(splittedLine[editedAtColumnIndex],
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // TODO: mby error handling
      ).toLocalDate();
      //      if (lastEditionDate.isEqual(matchingDate)) {
      mismatchedLines.add(line);
      //      }
    }
    return mismatchedLines;
  }

  private <T> List<T> mapObjects(MappingIterator<T> mappingIterator) {
    List<T> mappedObjects = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      mappedObjects.add(mappingIterator.next());
    }
    return mappedObjects;
  }

  private File downloadImportFileWithPrefix(String csvImportFilePrefix) throws IOException {
    List<String> foundImportFileKeys = amazonService.getS3ObjectKeysFromPrefix(SERVICEPOINT_DIDOK_DIR_NAME, csvImportFilePrefix);
    String fileKeyToDownload = handleImportFileKeysResult(foundImportFileKeys);
    File download = amazonService.pullFile(fileKeyToDownload);
    log.info("Name: " + download.getName() + ", size: " + download.length() + " bytes");
    return download;
  }

  private String handleImportFileKeysResult(List<String> importFileKeys) {
    if (importFileKeys.isEmpty()) {
      throw new RuntimeException("[IMPORT]: Not found file on S3");
    } else if (importFileKeys.size() > 1) {
      throw new RuntimeException("[IMPORT]: Found more than 1 file to download on S3");
    }
    return importFileKeys.get(0);
  }

  private String attachTodayDate(String csvImportFilePrefix) {
    LocalDate today = LocalDate.now();
    return csvImportFilePrefix + replaceHyphensWithUnderscores(today.toString());
  }

  private String attachYesterdayDate(String csvImportFilePrefix) {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    return csvImportFilePrefix + replaceHyphensWithUnderscores(yesterday.toString());
  }

  private String replaceHyphensWithUnderscores(String input) {
    return input.replaceAll("-", "");
  }

}
