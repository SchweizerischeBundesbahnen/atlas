package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ServicePointBulkImportApiV1;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointBulkImportService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointBulkImportController implements ServicePointBulkImportApiV1 {

  private final ServicePointBulkImportService servicePointBulkImportService;
  private final AtlasExceptionHandler atlasExceptionHandler;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportUpdate(
      List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers) {
    List<BulkImportItemExecutionResult> results = new ArrayList<>();
    bulkImportContainers.forEach(bulkImportContainer -> {
      try {
        servicePointBulkImportService.updateServicePointByUserName("u236171", bulkImportContainer);
        //        servicePointBulkImportService.updateServicePoint(bulkImportContainer);

        results.add(BulkImportItemExecutionResult.builder()
            .lineNumber(bulkImportContainer.getLineNumber())
            .build());
      } catch (Exception exception) {
        results.add(BulkImportItemExecutionResult.builder()
            .lineNumber(bulkImportContainer.getLineNumber())
            .errorResponse(atlasExceptionHandler.mapToErrorResponse(exception))
            .build());
      }
    });
    return results;
  }

}
