package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.LineBulkImportApiInternal;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.line.directory.service.bulk.LineBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LineBulkImportControllerInternal extends BaseBulkImportControllerInternal implements LineBulkImportApiInternal {

  private final LineBulkImportService lineBulkImportService;

  @Override
  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).UPDATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)""")
  public List<BulkImportItemExecutionResult> lineUpdate(
      List<BulkImportUpdateContainer<LineUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        lineBulkImportService::updateLineByUsername,
        lineBulkImportService::updateLine);
  }

}
