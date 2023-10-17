package ch.sbb.importservice.service.csv;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModelContainer;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StopPlaceCsvService extends CsvService<StopPlaceCsvModel> {

  public static final String PRM_STOP_PLACES_FILE_NAME = "PRM_STOP_PLACES";
  public static final int ACTIVE_STATUS = 1;
  public static final String UNKNOWN_MEANS_OF_TRANSPORT_CODE = "~0~";

  protected StopPlaceCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected CsvFileNameModel defineCsvFileName() {
    return CsvFileNameModel.builder().fileName(PRM_STOP_PLACES_FILE_NAME).addDateToPostfix(true).build();
  }

  @Override
  protected String getModifiedDateHeader() {
    return EDITED_AT_COLUMN_NAME_PRM;
  }

  @Override
  protected String getImportCsvJobName() {
    return JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
  }

  @Override
  protected Class<StopPlaceCsvModel> getType() {
    return StopPlaceCsvModel.class;
  }

  public List<StopPlaceCsvModelContainer> mapToStopPlaceCsvModelContainers(
      List<StopPlaceCsvModel> stopPlaceCsvModels) {
    List<StopPlaceCsvModel> activeStopPlaces = filterActiveStopPlaces(stopPlaceCsvModels);
    Map<Integer, List<StopPlaceCsvModel>> stopPlaceGroupedByDidokCode = activeStopPlaces.stream()
        .collect(Collectors.groupingBy(StopPlaceCsvModel::getDidokCode));
    List<StopPlaceCsvModelContainer> stopPlaceCsvModelContainers = new ArrayList<>();
    stopPlaceGroupedByDidokCode.forEach((key, value) -> {
      StopPlaceCsvModelContainer servicePointCsvModelContainer = StopPlaceCsvModelContainer.builder()
          .didokCode(key)
          .stopPlaceCsvModels(value)
          .build();
      value.sort(Comparator.comparing(BasePrmCsvModel::getValidFrom));
      stopPlaceCsvModelContainers.add(servicePointCsvModelContainer);
    });

    mergeSequentialEqualsVersions(stopPlaceCsvModelContainers);
    mergeEqualsVersions(stopPlaceCsvModelContainers);
    return stopPlaceCsvModelContainers;
  }

  private void mergeSequentialEqualsVersions(List<StopPlaceCsvModelContainer> servicePointCsvModelContainers) {
    log.info("Starting checking sequential equals StopPlace versions...");
    List<Integer> mergedDidokNumbers = new ArrayList<>();
    servicePointCsvModelContainers.forEach(
        container -> container.setStopPlaceCsvModels(mergeSequentialEqualsStopPlaceVersions(container.getStopPlaceCsvModels(),
            mergedDidokNumbers)));
    log.info("Total Merged sequential StopPlace versions {}", mergedDidokNumbers.size());
    log.info("Merged StopPlace Didok numbers {}", mergedDidokNumbers);
  }

  private void mergeEqualsVersions(List<StopPlaceCsvModelContainer> servicePointCsvModelContainers) {
    log.info("Starting checking equals StopPlace versions...");
    List<Integer> mergedDidokNumbers = new ArrayList<>();
    servicePointCsvModelContainers.forEach(
        container -> container.setStopPlaceCsvModels(mergeEqualsStopPlaceVersions(container.getStopPlaceCsvModels(),
            mergedDidokNumbers)));
    log.info("Total Merged equals StopPlace versions {}", mergedDidokNumbers.size());
    log.info("Merged equals StopPlace Didok numbers {}", mergedDidokNumbers);
  }

  private static List<StopPlaceCsvModel> filterActiveStopPlaces(List<StopPlaceCsvModel> models) {
    List<StopPlaceCsvModel> stopPlaceCsvModels = removeWrongMeansOfTransportCode(models);
    log.info("Found and removed {} StopPlace versions with [~0~] TRANSPORTATION_MEANS code.",
        models.size() - stopPlaceCsvModels.size());
    List<StopPlaceCsvModel> activeStopPlaceVersions = getActiveStopPlaceVersions(stopPlaceCsvModels);
    log.info("Found and removed {} inactive (STATUS=0) StopPlace versions.",
        stopPlaceCsvModels.size() - activeStopPlaceVersions.size());
    return activeStopPlaceVersions;
  }

  private static List<StopPlaceCsvModel> getActiveStopPlaceVersions(List<StopPlaceCsvModel> stopPlaceCsvModels) {
    return stopPlaceCsvModels.stream()
        .filter(stopPlaceCsvModel -> stopPlaceCsvModel.getStatus() == ACTIVE_STATUS).toList();
  }

  private static List<StopPlaceCsvModel> removeWrongMeansOfTransportCode(List<StopPlaceCsvModel> stopPlaceCsvModels) {
    return stopPlaceCsvModels.stream()
        .filter(stopPlaceCsvModel -> !stopPlaceCsvModel.getTransportationMeans().equals(UNKNOWN_MEANS_OF_TRANSPORT_CODE))
        .toList();
  }

  public List<StopPlaceCsvModel> mergeSequentialEqualsStopPlaceVersions(List<StopPlaceCsvModel> stopPlaceCsvModels,
      List<Integer> mergedDidokNumbers) {
    List<StopPlaceCsvModel> stopPlaceCsvModelListMerged = new ArrayList<>();
    if (stopPlaceCsvModels.size() == 1){
        return stopPlaceCsvModels;
    }
    if (stopPlaceCsvModels.size() > 1) {
      stopPlaceCsvModels.sort(comparing(StopPlaceCsvModel::getValidFrom));
       stopPlaceCsvModelListMerged = new ArrayList<>(List.of(stopPlaceCsvModels.get(0)));
      for (int currentIndex = 1; currentIndex < stopPlaceCsvModels.size(); currentIndex++) {
        final StopPlaceCsvModel previous = stopPlaceCsvModelListMerged.get(stopPlaceCsvModelListMerged.size() - 1);
        final StopPlaceCsvModel current = stopPlaceCsvModels.get(currentIndex);
        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom())
            && current.equals(previous)) {
          removeCurrentVersionIncreaseNextValidTo(previous, current);
          mergedDidokNumbers.add(current.getDidokCode());
        } else {
          stopPlaceCsvModelListMerged.add(current);
        }
      }
    }
    return stopPlaceCsvModelListMerged;
  }

  public List<StopPlaceCsvModel> mergeEqualsStopPlaceVersions(List<StopPlaceCsvModel> stopPlaceCsvModels,
      List<Integer> mergedDidokNumbers) {
    List<StopPlaceCsvModel> stopPlaceCsvModelListMerged = new ArrayList<>();
    if (stopPlaceCsvModels.size() == 1){
      return stopPlaceCsvModels;
    }
    if (stopPlaceCsvModels.size() > 1) {
      stopPlaceCsvModels.sort(comparing(StopPlaceCsvModel::getValidFrom));
      stopPlaceCsvModelListMerged = new ArrayList<>(
          List.of(stopPlaceCsvModels.get(0))
      );
      for (int currentIndex = 1; currentIndex < stopPlaceCsvModels.size(); currentIndex++) {
        final StopPlaceCsvModel previous = stopPlaceCsvModelListMerged.get(stopPlaceCsvModelListMerged.size() - 1);
        final StopPlaceCsvModel current = stopPlaceCsvModels.get(currentIndex);
        if (current.getValidFrom().isEqual(previous.getValidFrom()) && current.getValidTo().isEqual(previous.getValidTo())
            && current.equals(previous)) {
          log.info("Found duplicated version with number {}", previous.getDidokCode());
          log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
          log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
          mergedDidokNumbers.add(current.getDidokCode());
        } else {
          stopPlaceCsvModelListMerged.add(current);
        }
      }
    }
    return stopPlaceCsvModelListMerged;
  }

  private void removeCurrentVersionIncreaseNextValidTo(StopPlaceCsvModel previous,
      StopPlaceCsvModel current) {
    log.info("Found versions to merge with number {}", previous.getDidokCode());
    log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
    log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
    previous.setValidTo(current.getValidTo());
    log.info("Version merged [{}]-[{}]", previous.getValidFrom(), current.getValidTo());
  }

}
