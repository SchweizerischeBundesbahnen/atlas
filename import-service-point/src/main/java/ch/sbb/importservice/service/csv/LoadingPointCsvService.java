package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
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
public class LoadingPointCsvService extends CsvService<LoadingPointCsvModel> {

  LoadingPointCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected String getFilePrefix() {
    return FileHelperService.LOADING_POINT_FILE_PREFIX;
  }

  @Override
  protected String getImportCsvJobName() {
    return JobDescriptionConstants.IMPORT_LOADING_POINT_CSV_JOB_NAME;
  }

  @Override
  protected Class<LoadingPointCsvModel> getType() {
    return LoadingPointCsvModel.class;
  }

  public List<LoadingPointCsvModelContainer> mapToLoadingPointCsvModelContainers(
      final List<LoadingPointCsvModel> loadingPointCsvModels) {
    final Map<Integer, List<LoadingPointCsvModel>> groupedByDidokCode = loadingPointCsvModels.stream()
        .collect(Collectors.groupingBy(LoadingPointCsvModel::getServicePointNumber));

    final List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers = new ArrayList<>();
    groupedByDidokCode.forEach((didokCode, csvModelsByDidokCode) -> {
      final Map<Integer, List<LoadingPointCsvModel>> groupedByLoadingPointNumber = csvModelsByDidokCode.stream()
          .collect(Collectors.groupingBy(LoadingPointCsvModel::getNumber));

      groupedByLoadingPointNumber.forEach((number, csvModelsByLoadingPointNumber) -> {
        csvModelsByLoadingPointNumber.sort(Comparator.comparing(BaseDidokCsvModel::getValidFrom));
        final LoadingPointCsvModelContainer loadingPointCsvModelContainer = LoadingPointCsvModelContainer
            .builder()
            .didokCode(didokCode)
            .loadingPointNumber(number)
            .csvModelList(csvModelsByLoadingPointNumber)
            .build();
        loadingPointCsvModelContainer.mergeWhenDatesAreSequentialAndModelsAreEqual();
        loadingPointCsvModelContainers.add(loadingPointCsvModelContainer);
      });
    });
    logInfo(loadingPointCsvModelContainers, loadingPointCsvModels);
    return loadingPointCsvModelContainers;
  }

  private void logInfo(List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers,
      List<LoadingPointCsvModel> loadingPointCsvModels) {
    final long numberOfAllCsvModelsAfterMerge = loadingPointCsvModelContainers.stream()
        .collect(Collectors.summarizingInt(container -> container.getCsvModelList().size())).getSum();
    log.info("Found {} LoadingPointCsvModelContainers with {} LoadingPointCsvModels to send to ServicePointDirectory",
        loadingPointCsvModelContainers.size(), loadingPointCsvModels.size());
    log.info("Merged {} LoadingPointCsvModels ", loadingPointCsvModels.size() - numberOfAllCsvModelsAfterMerge);
    log.info("Total LoadingPointCsvModels to process {}", numberOfAllCsvModelsAfterMerge);
  }

}
