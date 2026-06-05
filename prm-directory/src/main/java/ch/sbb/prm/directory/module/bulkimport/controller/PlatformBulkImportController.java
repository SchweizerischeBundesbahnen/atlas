package ch.sbb.prm.directory.module.bulkimport.controller;

import ch.sbb.atlas.api.prm.PlatformBulkImportApi;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.model.terminate.PlatformTerminateCsvModel;
import ch.sbb.prm.directory.module.bulkimport.service.PlatformBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PlatformBulkImportController extends BaseBulkImportControllerInternal implements PlatformBulkImportApi {

  private final PlatformBulkImportService platformBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportPlatformReducedUpdate(
      List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        platformBulkImportService::updatePlatformReducedByUsername,
        platformBulkImportService::updatePlatformReduced);
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportPlatformCompleteUpdate(
      List<BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel>> bulkImportUpdateContainers) {
    return executeBulkImport(bulkImportUpdateContainers,
        platformBulkImportService::updatePlatformCompleteByUsername,
        platformBulkImportService::updatePlatformComplete);
  }

  @Override
  public List<BulkImportItemExecutionResult> bulkImportTerminate(
      List<BulkImportUpdateContainer<PlatformTerminateCsvModel>> bulkImportUpdateContainers) {
    return executeBulkImport(bulkImportUpdateContainers,
        platformBulkImportService::terminatePlatformByUsername,
        platformBulkImportService::terminatePlatform);
  }
}
