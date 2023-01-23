package ch.sbb.importservice.service;

import ch.sbb.atlas.base.service.amazon.service.AmazonService;
import ch.sbb.atlas.base.service.imports.DidokCsvMapper;
import ch.sbb.atlas.base.service.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {

  private static final String CSV_DATE_TIME_FORMATTING = "yyyy-MM-dd HH:mm:ss";
  private static final String HASHTAG = "#";
  private static final String SERVICEPOINT_DIDOK_DIR_NAME = "servicepoint_didok";
  private static final String CSV_DELIMITER = ";";
  private static final String EDITED_AT_COLUMN_NAME = "GEAENDERT_AM";
  private static final String DINSTELLE_FILE_PREFIX = "DIDOK3_DIENSTSTELLEN_ALL_V_3_";
  private static final String LADESTELLEN_FILE_PREFIX = "DIDOK3_LADESTELLEN_";

  private final AmazonService amazonService;

  private final JobHelperService jobHelperService;

  @Setter
  private LocalDate matchingDate = LocalDate.now();

  public List<ServicePointCsvModelContainer> getActualServicePotinCsvModelsFromS3(String jobName) throws IOException {
    File importFile = downloadImportFile(DINSTELLE_FILE_PREFIX, jobName);
    List<ServicePointCsvModel> servicePointCsvModels = getCsvModelsToUpdate(importFile, ServicePointCsvModel.class);

    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = mapToServicePointCsvModelContainers(
        servicePointCsvModels);

    log.info("servicePointCsvModelContainers size: {}", servicePointCsvModelContainers.size());
    return servicePointCsvModelContainers;
  }

  private List<ServicePointCsvModelContainer> mapToServicePointCsvModelContainers(
      List<ServicePointCsvModel> servicePointCsvModels) {
    Map<Integer, List<ServicePointCsvModel>> servicePointGrouppedByDidokCode = servicePointCsvModels.stream()
        .collect(Collectors.groupingBy(ServicePointCsvModel::getDidokCode));
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointGrouppedByDidokCode.forEach((key, value) -> {
      ServicePointCsvModelContainer servicePointCsvModelContainer = ServicePointCsvModelContainer.builder()
          .didokCode(key)
          .servicePointCsvModelList(value)
          .build();
      value.sort(Comparator.comparing(BaseDidokCsvModel::getValidFrom));
      servicePointCsvModelContainers.add(servicePointCsvModelContainer);
    });
    return servicePointCsvModelContainers;
  }

  public List<LoadingPointCsvModel> getActualLoadingPotinCsvModelsFromS3(String jobName) throws IOException {
    File importFile = downloadImportFile(LADESTELLEN_FILE_PREFIX, jobName);
    List<LoadingPointCsvModel> loadingPointCsvModels = getCsvModelsToUpdate(importFile, LoadingPointCsvModel.class);
    log.info("loadingPointCsvModels size: {}", loadingPointCsvModels.size());
    return loadingPointCsvModels;
  }

  public List<ServicePointCsvModelContainer> getActualServicePotinCsvModelsFromS3(File file) throws IOException {
    List<ServicePointCsvModel> servicePointCsvModels = getCsvModelsToUpdate(file, ServicePointCsvModel.class);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = mapToServicePointCsvModelContainers(
        servicePointCsvModels);
    log.info("servicePointCsvModelsContainer size: {}", servicePointCsvModelContainers.size());
    return servicePointCsvModelContainers;
  }

  public File downloadImportFile(String csvImportFilePrefix, String jobName) throws IOException {
    LocalDate dateForImportFileToDownload = jobHelperService.getDateForImportFileToDownload(jobName);
    this.setMatchingDate(dateForImportFileToDownload);
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
          DateTimeFormatter.ofPattern(CSV_DATE_TIME_FORMATTING) // TODO: mby error handling
      ).toLocalDate();
      boolean dateMatchedBetweenTodayAndMatchingDate = jobHelperService.isDateMatchedBetweenTodayAndMatchingDate(matchingDate,
          lastEditionDate);
      if (dateMatchedBetweenTodayAndMatchingDate) {
        mismatchedLines.add(line);
      }
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
    String fileKeyToDownload = handleImportFileKeysResult(foundImportFileKeys, csvImportFilePrefix);
    File download = amazonService.pullFile(fileKeyToDownload);
    log.info("Name: " + download.getName() + ", size: " + download.length() + " bytes");
    return download;
  }

  private String handleImportFileKeysResult(List<String> importFileKeys, String csvImportFilePrefix) {
    if (importFileKeys.isEmpty()) {
      //TODO: create custom Exception
      throw new RuntimeException("[IMPORT]: File " + csvImportFilePrefix + " not found file on S3");
    } else if (importFileKeys.size() > 1) {
      throw new RuntimeException("[IMPORT]: Found more than 1 file " + csvImportFilePrefix + " to download on S3");
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
