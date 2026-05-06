package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import ch.sbb.atlas.api.servicepoint.ServicePointBulkImportApi;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.service.ServicePointBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ServicePointBulkImportController extends BaseBulkImportControllerInternal implements ServicePointBulkImportApi {

  private final ServicePointBulkImportService servicePointBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportUpdate(
      List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        servicePointBulkImportService::updateServicePointByUserName,
        servicePointBulkImportService::updateServicePoint);
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportCreate(
      List<BulkImportUpdateContainer<ServicePointCreateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        servicePointBulkImportService::createServicePointByUserName,
        servicePointBulkImportService::createServicePoint);
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportTerminate(
      List<BulkImportUpdateContainer<ServicePointTerminateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        servicePointBulkImportService::terminateServicePointByUserName,
        servicePointBulkImportService::terminateServicePoint);
  }
}
