package ch.sbb.atlas.servicepointdirectory.module.bulkimport.trafficpoint.controller;

import ch.sbb.atlas.api.servicepoint.TrafficPointBulkImportApi;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.TrafficPointTerminateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.trafficpoint.service.TrafficPointElementBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TrafficPointElementBulkImportController extends BaseBulkImportControllerInternal implements
    TrafficPointBulkImportApi {

  private final TrafficPointElementBulkImportService trafficPointElementBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportCreate(
      List<BulkImportUpdateContainer<TrafficPointCreateCsvModel>> bulkImportCreateContainers) {
    return executeBulkImport(bulkImportCreateContainers,
        trafficPointElementBulkImportService::createTrafficPointByUserName,
        trafficPointElementBulkImportService::createTrafficPoint);
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportUpdate(
      List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        trafficPointElementBulkImportService::updateTrafficPointByUserName,
        trafficPointElementBulkImportService::updateTrafficPoint);
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportTerminate(
      List<BulkImportUpdateContainer<TrafficPointTerminateCsvModel>> bulkImportTerminateContainers) {
    return executeBulkImport(bulkImportTerminateContainers,
        trafficPointElementBulkImportService::terminateTrafficPointByUserName,
        trafficPointElementBulkImportService::terminateTrafficPoint);
  }

}
