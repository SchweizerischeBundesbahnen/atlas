package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.LineBulkImportApiV1;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportController;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.line.directory.service.bulk.LineBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LineBulkImportController extends BaseBulkImportController implements LineBulkImportApiV1 {

  private final LineBulkImportService lineBulkImportService;

  @Override
  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)""")
  public List<BulkImportItemExecutionResult> lineUpdate(
      List<BulkImportUpdateContainer<LineUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        lineBulkImportService::updateLineByUsername,
        lineBulkImportService::updateLine);
  }

}
