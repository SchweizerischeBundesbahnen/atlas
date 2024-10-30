package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ServicePointBulkImportApiV1;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk.ServicePointBulkImportService;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServicePointBulkImportController extends BaseBulkImportController implements ServicePointBulkImportApiV1 {

  private final ServicePointBulkImportService servicePointBulkImportService;

  public ServicePointBulkImportController(
      AtlasExceptionHandler atlasExceptionHandler,
      ServicePointBulkImportService servicePointBulkImportService) {
    super(atlasExceptionHandler);
    this.servicePointBulkImportService = servicePointBulkImportService;
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportUpdate(List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        servicePointBulkImportService::updateServicePointByUserName,
        servicePointBulkImportService::updateServicePoint);
  }

}
