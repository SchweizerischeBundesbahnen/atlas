package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.PlatformBulkImportApiV1;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.PlatformReducedUpdateCsvModel;
import ch.sbb.prm.directory.service.PlatformBulkImportService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlatformBulkImportController implements PlatformBulkImportApiV1 {

  private final PlatformBulkImportService platformBulkImportService;
  private final AtlasExceptionHandler atlasExceptionHandler;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportPlatformReducedUpdate(List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> bulkImportContainers) {
    List<BulkImportItemExecutionResult> results = new ArrayList<>();
    bulkImportContainers.forEach(bulkImportContainer -> {
      try {
        if (bulkImportContainer.getInNameOf() != null) {
          platformBulkImportService.updatePlatformReducedByUsername(bulkImportContainer.getInNameOf(), bulkImportContainer);
        } else {
          platformBulkImportService.updatePlatformReduced(bulkImportContainer);
        }
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
