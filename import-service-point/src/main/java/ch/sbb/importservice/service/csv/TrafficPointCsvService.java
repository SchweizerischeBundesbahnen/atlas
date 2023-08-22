package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import ch.sbb.importservice.utils.JobDescriptionConstants;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrafficPointCsvService extends CsvService<TrafficPointElementCsvModel> {

  protected TrafficPointCsvService(FileHelperService fileHelperService,
      JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected String getFilePrefix() {
    return FileHelperService.TRAFFIC_POINT_FILE_PREFIX;
  }

  @Override
  protected String getImportCsvJobName() {
    return JobDescriptionConstants.IMPORT_TRAFFIC_POINT_CSV_JOB_NAME;
  }

  @Override
  protected Class<TrafficPointElementCsvModel> getType() {
    return TrafficPointElementCsvModel.class;
  }

  public List<TrafficPointCsvModelContainer> mapToTrafficPointCsvModelContainers(
      List<TrafficPointElementCsvModel> trafficPointElementCsvModels) {
    final Map<String, List<TrafficPointElementCsvModel>> trafficPointsGroupedBySloid = trafficPointElementCsvModels
        .stream()
        .collect(Collectors.groupingBy(TrafficPointElementCsvModel::getSloid));

    final List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = trafficPointsGroupedBySloid
        .keySet()
        .stream()
        .map(sloid -> {
          final List<TrafficPointElementCsvModel> trafficPointCsvModelGroup = trafficPointsGroupedBySloid.get(sloid);
          trafficPointCsvModelGroup.sort(Comparator.comparing(BaseDidokCsvModel::getValidFrom));
          final TrafficPointCsvModelContainer trafficPointCsvModelContainer = TrafficPointCsvModelContainer
              .builder()
              .sloid(sloid)
              .csvModelList(trafficPointCsvModelGroup)
              .build();
          trafficPointCsvModelContainer.mergeWhenDatesAreSequentialAndModelsAreEqual();
          return trafficPointCsvModelContainer;
        }).collect(Collectors.toList());

    logInfo(trafficPointCsvModelContainers, trafficPointElementCsvModels);
    return trafficPointCsvModelContainers;
  }

  private void logInfo(List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers,
      List<TrafficPointElementCsvModel> trafficPointCsvModels) {
    final long numberOfAllCsvModelsAfterMerge = trafficPointCsvModelContainers.stream()
        .collect(Collectors.summarizingInt(container -> container.getCsvModelList().size())).getSum();
    log.info("Found {} TrafficPointCsvModelContainers with {} TrafficPointCsvModels to send to ServicePointDirectory",
        trafficPointCsvModelContainers.size(), trafficPointCsvModels.size());
    log.info("Merged {} TrafficPointCsvModels ", trafficPointCsvModels.size() - numberOfAllCsvModelsAfterMerge);
    log.info("Total TrafficPointCsvModels to process {}", numberOfAllCsvModelsAfterMerge);
  }

}
