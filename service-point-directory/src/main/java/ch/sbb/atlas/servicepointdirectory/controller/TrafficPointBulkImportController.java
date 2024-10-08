package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.TrafficPointBulkImportApiV1;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointBulkImportService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficPointBulkImportController implements TrafficPointBulkImportApiV1 {

  private final TrafficPointBulkImportService trafficPointBulkImportService;
  private final AtlasExceptionHandler atlasExceptionHandler;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportUpdate(List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> bulkImportContainers) {
    List<BulkImportItemExecutionResult> results = new ArrayList<>();
    bulkImportContainers.forEach(bulkImportContainer -> {
      try {
        if (bulkImportContainer.getInNameOf() != null) {
          trafficPointBulkImportService.updateTrafficPointByUserName(bulkImportContainer.getInNameOf(), bulkImportContainer);
        } else {
          trafficPointBulkImportService.updateTrafficPoint(bulkImportContainer);
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
