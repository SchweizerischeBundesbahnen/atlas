package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.PlatformBulkImportApiV1;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.prm.directory.service.bulk.PlatformBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PlatformBulkImportController extends BaseBulkImportControllerInternal implements PlatformBulkImportApiV1 {

  private final PlatformBulkImportService platformBulkImportService;

  @Override
  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).UPDATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  public List<BulkImportItemExecutionResult> bulkImportPlatformReducedUpdate(
      List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        platformBulkImportService::updatePlatformReducedByUsername,
        platformBulkImportService::updatePlatformReduced);
  }

}
