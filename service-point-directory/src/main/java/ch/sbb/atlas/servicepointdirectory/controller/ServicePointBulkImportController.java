package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ServicePointBulkImportApiV1;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.model.exception.AtlasException;
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

  @Override
  public List<ItemImportResult> bulkImportUpdate(List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers) {
    List<ItemImportResult> results = new ArrayList<>();
    bulkImportContainers.forEach(bulkImportContainer -> {
      try {
        servicePointBulkImportService.updateServicePoint(bulkImportContainer);
        results.add(ItemImportResult.successResultBuilder().build());
      } catch (AtlasException atlasException) {
        results.add(ItemImportResult.failedResultBuilder(atlasException).build());
      }
    });
    return results;
  }

}
