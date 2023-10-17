package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModel;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModelContainer;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
    List<StopPlaceCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    stopPlaceGroupedByDidokCode.forEach((key, value) -> {
      StopPlaceCsvModelContainer servicePointCsvModelContainer = StopPlaceCsvModelContainer.builder()
          .didokCode(key)
          .stopPlaceCsvModels(value)
          .build();
      value.sort(Comparator.comparing(BasePrmCsvModel::getValidFrom));
      servicePointCsvModelContainers.add(servicePointCsvModelContainer);
    });
    return servicePointCsvModelContainers;
  }

  private static List<StopPlaceCsvModel> filterActiveStopPlaces(List<StopPlaceCsvModel> models) {
    List<StopPlaceCsvModel> stopPlaceCsvModels = removeWrongMeansOfTransportCode(models);
    log.info("Found and removed {} StopPlace versions with [~0~] TRANSPORTATION_MEANS code.",
        models.size()-stopPlaceCsvModels.size());
    List<StopPlaceCsvModel> activeStopPlaceVersions = getActiveStopPlaceVersions(stopPlaceCsvModels);
    log.info("Found and removed {} inactive (STATUS=0) StopPlace versions.",
        stopPlaceCsvModels.size() - activeStopPlaceVersions.size() );
    List<StopPlaceCsvModel> distinctActiveStopPlaceCsvModels = removeDuplicatedVersions(activeStopPlaceVersions);
    log.info("Found and removed {} duplicated StopPlace versions.",
        activeStopPlaceVersions.size() - distinctActiveStopPlaceCsvModels.size() );
    log.info("Found {} StopPlace versions to be imported.",distinctActiveStopPlaceCsvModels.size());
    return distinctActiveStopPlaceCsvModels;
  }

  private static List<StopPlaceCsvModel> getActiveStopPlaceVersions(List<StopPlaceCsvModel> stopPlaceCsvModels) {
    return stopPlaceCsvModels.stream()
        .filter(stopPlaceCsvModel -> stopPlaceCsvModel.getStatus() == ACTIVE_STATUS).toList();
  }

  private static List<StopPlaceCsvModel> removeDuplicatedVersions(List<StopPlaceCsvModel> stopPlaceCsvModels) {
    return new ArrayList<>(new HashSet<>(stopPlaceCsvModels));
  }
  private static List<StopPlaceCsvModel> removeWrongMeansOfTransportCode(List<StopPlaceCsvModel> stopPlaceCsvModels) {
    return stopPlaceCsvModels.stream()
        .filter(stopPlaceCsvModel -> !stopPlaceCsvModel.getTransportationMeans().equals(UNKNOWN_MEANS_OF_TRANSPORT_CODE)).toList();
  }

}
