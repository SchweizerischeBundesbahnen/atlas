package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;
import static java.util.Comparator.comparing;

import ch.sbb.atlas.imports.prm.platform.PlatformCsvModel;
import ch.sbb.atlas.imports.prm.platform.PlatformCsvModelContainer;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModelContainer;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlatformCsvService extends CsvService<PlatformCsvModel> {

  public static final String PRM_STOP_PLACES_FILE_NAME = "PRM_PLATFORMS";
  public static final int ACTIVE_STATUS = 1;
  public static final String UNKNOWN_MEANS_OF_TRANSPORT_CODE = "~0~";

  protected PlatformCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected CsvFileNameModel csvFileNameModel() {
    return CsvFileNameModel.builder()
        .fileName(PRM_STOP_PLACES_FILE_NAME)
        .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
        .addDateToPostfix(true)
        .build();
  }

  @Override
  protected String getModifiedDateHeader() {
    return EDITED_AT_COLUMN_NAME_PRM;
  }

  @Override
  protected String getImportCsvJobName() {
    return JobDescriptionConstants.IMPORT_PLATFORM_CSV_JOB_NAME;
  }

  @Override
  protected Class<PlatformCsvModel> getType() {
    return PlatformCsvModel.class;
  }

  public List<PlatformCsvModelContainer> mapToPlatformCsvModelContainers(List<PlatformCsvModel> platformCsvModels) {

    return Collections.emptyList();
  }

  private void mergeStopPoints(List<StopPointCsvModelContainer> stopPointCsvModelContainers) {
    mergeSequentialEqualsVersions(stopPointCsvModelContainers);
    mergeEqualsVersions(stopPointCsvModelContainers);
  }

  private static List<StopPointCsvModel> filterStopPoints(List<StopPointCsvModel> stopPointCsvModels) {
    List<StopPointCsvModel> activeStopPoints = filterActiveStopPoints(stopPointCsvModels);
    replaceWrongMeansOfTransportCode(stopPointCsvModels);
    return activeStopPoints;
  }

  private void mergeSequentialEqualsVersions(List<StopPointCsvModelContainer> servicePointCsvModelContainers) {
    log.info("Starting checking sequential equals StopPoint versions...");
    List<Integer> mergedDidokNumbers = new ArrayList<>();
    servicePointCsvModelContainers.forEach(
        container -> container.setStopPointCsvModels(mergeSequentialEqualsStopPointVersions(container.getStopPointCsvModels(),
            mergedDidokNumbers)));
    log.info("Total Merged sequential StopPoint versions {}", mergedDidokNumbers.size());
    log.info("Merged StopPoint Didok numbers {}", mergedDidokNumbers);
  }

  private void mergeEqualsVersions(List<StopPointCsvModelContainer> servicePointCsvModelContainers) {
    log.info("Starting checking equals StopPoint versions...");
    List<Integer> mergedDidokNumbers = new ArrayList<>();
    servicePointCsvModelContainers.forEach(
        container -> container.setStopPointCsvModels(mergeEqualsStopPointVersions(container.getStopPointCsvModels(),
            mergedDidokNumbers)));
    log.info("Total Merged equals StopPoint versions {}", mergedDidokNumbers.size());
    log.info("Merged equals StopPoint Didok numbers {}", mergedDidokNumbers);
  }

  private static List<StopPointCsvModel> filterActiveStopPoints(List<StopPointCsvModel> models) {
    List<StopPointCsvModel> activeStopPointVersions = getActiveStopPointsVersions(models);
    log.info("Found and removed {} inactive (STATUS=0) StopPoint versions.",
        models.size() - activeStopPointVersions.size());
    return activeStopPointVersions;
  }

  private static List<StopPointCsvModel> getActiveStopPointsVersions(List<StopPointCsvModel> stopPointCsvModels) {
    return stopPointCsvModels.stream()
        .filter(stopPointCsvModel -> stopPointCsvModel.getStatus() == ACTIVE_STATUS).toList();
  }

  private static void replaceWrongMeansOfTransportCode(List<StopPointCsvModel> stopPointCsvModels) {
    stopPointCsvModels.forEach(stopPointCsvModel -> {
      if (stopPointCsvModel.getTransportationMeans().equals(UNKNOWN_MEANS_OF_TRANSPORT_CODE)) {
        stopPointCsvModel.setTransportationMeans("~U~");
        log.info("Found StopPoint versions {} with [~0~] TRANSPORTATION_MEANS code and change to UNKNOWN ({})",
            stopPointCsvModel.getDidokCode(), stopPointCsvModel.getTransportationMeans());
      }
    });
  }

  private List<StopPointCsvModel> mergeSequentialEqualsStopPointVersions(List<StopPointCsvModel> stopPointCsvModels,
      List<Integer> mergedDidokNumbers) {
    List<StopPointCsvModel> stopPointCsvModelListMerged = new ArrayList<>();
    if (stopPointCsvModels.size() == 1) {
      return stopPointCsvModels;
    }
    if (stopPointCsvModels.size() > 1) {
      stopPointCsvModels.sort(comparing(StopPointCsvModel::getValidFrom));
      stopPointCsvModelListMerged = new ArrayList<>(List.of(stopPointCsvModels.get(0)));
      for (int currentIndex = 1; currentIndex < stopPointCsvModels.size(); currentIndex++) {
        final StopPointCsvModel previous = stopPointCsvModelListMerged.get(stopPointCsvModelListMerged.size() - 1);
        final StopPointCsvModel current = stopPointCsvModels.get(currentIndex);
        if (DateHelper.areDatesSequential(previous.getValidTo(), current.getValidFrom())
            && current.equals(previous)) {
          removeCurrentVersionIncreaseNextValidTo(previous, current);
          mergedDidokNumbers.add(current.getDidokCode());
        } else {
          stopPointCsvModelListMerged.add(current);
        }
      }
    }
    return stopPointCsvModelListMerged;
  }

  private List<StopPointCsvModel> mergeEqualsStopPointVersions(List<StopPointCsvModel> stopPointCsvModels,
      List<Integer> mergedDidokNumbers) {
    List<StopPointCsvModel> stopPointCsvModelListMerged = new ArrayList<>();
    if (stopPointCsvModels.size() == 1) {
      return stopPointCsvModels;
    }
    if (stopPointCsvModels.size() > 1) {
      stopPointCsvModels.sort(comparing(StopPointCsvModel::getValidFrom));
      stopPointCsvModelListMerged = new ArrayList<>(
          List.of(stopPointCsvModels.get(0))
      );
      for (int currentIndex = 1; currentIndex < stopPointCsvModels.size(); currentIndex++) {
        final StopPointCsvModel previous = stopPointCsvModelListMerged.get(stopPointCsvModelListMerged.size() - 1);
        final StopPointCsvModel current = stopPointCsvModels.get(currentIndex);
        if (current.getValidFrom().isEqual(previous.getValidFrom()) && current.getValidTo().isEqual(previous.getValidTo())
            && current.equals(previous)) {
          log.info("Found duplicated version with number {}", previous.getDidokCode());
          log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
          log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
          mergedDidokNumbers.add(current.getDidokCode());
        } else {
          stopPointCsvModelListMerged.add(current);
        }
      }
    }
    return stopPointCsvModelListMerged;
  }

  private void removeCurrentVersionIncreaseNextValidTo(StopPointCsvModel previous,
      StopPointCsvModel current) {
    log.info("Found versions to merge with number {}", previous.getDidokCode());
    log.info("Version-1 [{}]-[{}]", previous.getValidFrom(), previous.getValidTo());
    log.info("Version-2 [{}]-[{}]", current.getValidFrom(), current.getValidTo());
    previous.setValidTo(current.getValidTo());
    log.info("Version merged [{}]-[{}]", previous.getValidFrom(), current.getValidTo());
  }

}
