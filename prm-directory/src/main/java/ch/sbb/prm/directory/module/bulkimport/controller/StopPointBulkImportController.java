package ch.sbb.prm.directory.module.bulkimport.controller;

import ch.sbb.atlas.api.prm.StopPointBulkImportApi;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.prm.directory.module.bulkimport.service.StopPointBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StopPointBulkImportController extends BaseBulkImportControllerInternal implements StopPointBulkImportApi {

  private final StopPointBulkImportService stopPointBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportStopPointTerminate(
      List<BulkImportUpdateContainer<SloidTerminateCsvModel>> bulkImportUpdateContainers) {
    return executeBulkImport(bulkImportUpdateContainers,
        stopPointBulkImportService::terminateStopPointByUsername,
        stopPointBulkImportService::terminateStopPoint);
  }
}
