package ch.sbb.importservice.service.csv;

import static ch.sbb.importservice.service.csv.CsvFileNameModel.SERVICEPOINT_DIDOK_DIR_NAME;

import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
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
public class ServicePointCsvService extends CsvService<ServicePointCsvModel> {

  public static final String SERVICE_POINT_FILE_PREFIX = "DIDOK3_DIENSTSTELLEN_ALL_V_3_";

  protected ServicePointCsvService(FileHelperService fileHelperService, JobHelperService jobHelperService) {
    super(fileHelperService, jobHelperService);
  }

  @Override
  protected CsvFileNameModel csvFileNameModel() {
    return CsvFileNameModel.builder()
        .fileName(SERVICE_POINT_FILE_PREFIX)
        .s3BucketDir(SERVICEPOINT_DIDOK_DIR_NAME)
        .addDateToPostfix(true)
        .build();
  }

  @Override
  protected String getModifiedDateHeader() {
    return EDITED_AT_COLUMN_NAME_SERVICE_POINT;
  }

  @Override
  protected String getImportCsvJobName() {
    return JobDescriptionConstants.IMPORT_SERVICE_POINT_CSV_JOB_NAME;
  }

  @Override
  protected Class<ServicePointCsvModel> getType() {
    return ServicePointCsvModel.class;
  }

  public List<ServicePointCsvModelContainer> mapToServicePointCsvModelContainers(
      List<ServicePointCsvModel> servicePointCsvModels) {
    Map<Integer, List<ServicePointCsvModel>> servicePointGroupedByDidokCode = servicePointCsvModels.stream()
        .collect(Collectors.groupingBy(ServicePointCsvModel::getDidokCode));
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    servicePointGroupedByDidokCode.forEach((key, value) -> {
      ServicePointCsvModelContainer servicePointCsvModelContainer = ServicePointCsvModelContainer.builder()
          .didokCode(key)
          .servicePointCsvModelList(value)
          .build();
      servicePointCsvModelContainer.mergeVersionsIsNotVirtualAndHasNotGeolocation();
      servicePointCsvModelContainer.mergeHasNotBezeichnungDiff();
      value.sort(Comparator.comparing(BaseDidokCsvModel::getValidFrom));
      servicePointCsvModelContainers.add(servicePointCsvModelContainer);
    });
    logInfo(servicePointCsvModels, servicePointCsvModelContainers);
    return servicePointCsvModelContainers;
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
