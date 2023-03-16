package ch.sbb.importservice.service;

import static ch.sbb.importservice.service.JobHelperService.MIN_LOCAL_DATE;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;

import ch.sbb.atlas.imports.DidokCsvMapper;
import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.importservice.exception.CsvException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {

  public static final String DINSTELLE_FILE_PREFIX = "DIDOK3_DIENSTSTELLEN_ALL_V_3_";
  public static final String LADESTELLEN_FILE_PREFIX = "DIDOK3_LADESTELLEN_";
  private static final String CSV_DATE_TIME_FORMATTING = "yyyy-MM-dd HH:mm:ss";
  private static final String HASHTAG = "#";
  private static final String CSV_DELIMITER = ";";
  private static final String EDITED_AT_COLUMN_NAME = "GEAENDERT_AM";
  private final FileHelperService fileHelperService;

  private final JobHelperService jobHelperService;

//  @Value("${amazon.bucketName}")
//  private String bucketName;

  public List<ServicePointCsvModelContainer> getActualServicePointCsvModelsFromS3() {
    log.info("Downloading file from Amazon S3 Bucket: {}");
    File file = fileHelperService.downloadImportFileFromS3(DINSTELLE_FILE_PREFIX);
    LocalDate matchingDate = jobHelperService.getDateForImportFileToDownload(IMPORT_SERVICE_POINT_CSV_JOB_NAME);
    log.info("CSV File to import: {}", file.getName());
    List<ServicePointCsvModel> servicePointCsvModels = getCsvModelsToUpdate(file, matchingDate, ServicePointCsvModel.class);
    fileHelperService.deleteConsumedFile(file);
    return mapToServicePointCsvModelContainers(
        servicePointCsvModels);
  }

  public List<ServicePointCsvModelContainer> getActualServicePointCsvModels(File file) {
    log.info("Starting Service Point import process");
    log.info("CSV File to import: {}", file.getName());
    List<ServicePointCsvModel> servicePointCsvModels = getCsvModelsToUpdate(file, MIN_LOCAL_DATE, ServicePointCsvModel.class);
    return mapToServicePointCsvModelContainers(
        servicePointCsvModels);
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
      servicePointCsvModelContainer.mergeVersionsIsNotVirtualAndHasNotGeolocation();
      servicePointCsvModelContainer.mergeHasJustBezeichnungDiff();
      value.sort(Comparator.comparing(BaseDidokCsvModel::getValidFrom));
      servicePointCsvModelContainers.add(servicePointCsvModelContainer);
    });
    logInfo(servicePointCsvModels, servicePointCsvModelContainers);
    return servicePointCsvModelContainers;
  }

  public List<LoadingPointCsvModel> getActualLoadingPointCsvModels(File file) {
    List<LoadingPointCsvModel> loadingPointCsvModels = getCsvModelsToUpdate(file, MIN_LOCAL_DATE,
        LoadingPointCsvModel.class);
    log.info("Found {} Loading Points to send to ServicePointDirectory", loadingPointCsvModels.size());
    return loadingPointCsvModels;
  }

  public List<LoadingPointCsvModel> getActualLoadingPointCsvModelsFromS3() {
    File importFile = fileHelperService.downloadImportFileFromS3(LADESTELLEN_FILE_PREFIX);
    List<LoadingPointCsvModel> loadingPointCsvModels = getCsvModelsToUpdate(importFile, MIN_LOCAL_DATE,
        LoadingPointCsvModel.class);
    log.info("Found {} Loading Points to send to ServicePointDirectory", loadingPointCsvModels.size());
    return loadingPointCsvModels;
  }

  public <T> List<T> getCsvModelsToUpdate(File importFile, LocalDate matchingDate, Class<T> type) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile))) {
      String headerLine = skipUntilHeaderLine(bufferedReader);
      int editedAtColumnIndex = getColumnIndexOfEditedAt(headerLine);
      List<String> mismatchedLines = getMismatchedLines(matchingDate, bufferedReader, editedAtColumnIndex);
      log.info("Found {} lines to update", mismatchedLines.size());
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
    int editedAtColumnIndex = Arrays.asList(attributes).indexOf(EDITED_AT_COLUMN_NAME);
    if (editedAtColumnIndex == -1) {
      throw new CsvException("Not found %s index".formatted(EDITED_AT_COLUMN_NAME));
    }
    return editedAtColumnIndex;
  }

  private List<String> getMismatchedLines(LocalDate matchingDate, BufferedReader bufferedReader, int editedAtColumnIndex)
      throws IOException {
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

  private void logInfo(List<ServicePointCsvModel> servicePointCsvModels,
      List<ServicePointCsvModelContainer> servicePointCsvModelContainers) {
    long prunedServicePointModels = servicePointCsvModelContainers.stream()
        .collect(Collectors.summarizingInt(value -> value.getServicePointCsvModelList().size())).getSum();
    List<Integer> mergedIsNotVirtualAndWithoutGeolocationNumbers = servicePointCsvModelContainers.stream()
        .filter(ServicePointCsvModelContainer::isHasMergedVersionNotVirtualWithoutGeolocation)
        .map(ServicePointCsvModelContainer::getDidokCode).toList();
    log.info("Merged IsNotVirtualAndWithoutGeolocationNumbers {}", mergedIsNotVirtualAndWithoutGeolocationNumbers.size());
    List<Integer> mergedHasJustBezeichningDiff = servicePointCsvModelContainers.stream()
        .filter(ServicePointCsvModelContainer::isHasJustBezeichnungDiffMerged)
        .map(ServicePointCsvModelContainer::getDidokCode).toList();
    log.info("Merged HasJustBezeichningDiff {}", mergedHasJustBezeichningDiff.size());

    log.info("Found {} ServicePointCsvModelContainers with {} ServicePointModels to send to ServicePointDirectory",
        servicePointCsvModelContainers.size(), servicePointCsvModels.size());
    log.info("Found and merged {} ServicePointCsvModels ", servicePointCsvModels.size() - prunedServicePointModels);
    log.info("Total ServicePointCsvModel to process {}", prunedServicePointModels);

    log.info("Merged ServicePointCsvModel IsNotVirtual without geolocation DidokNumber Lists: {}",
        mergedIsNotVirtualAndWithoutGeolocationNumbers);
    log.info("Merged ServicePointCsvModel hasJustBezeichnungDiff DidokNumber Lists: {}", mergedHasJustBezeichningDiff);
  }

}
