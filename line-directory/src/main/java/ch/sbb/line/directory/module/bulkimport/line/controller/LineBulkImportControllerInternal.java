package ch.sbb.line.directory.module.bulkimport.line.controller;

import ch.sbb.atlas.api.lidi.LineBulkImportApiInternal;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.line.directory.module.bulkimport.line.service.LineBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LineBulkImportControllerInternal extends BaseBulkImportControllerInternal implements LineBulkImportApiInternal {

  private final LineBulkImportService lineBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> lineCreate(
      List<BulkImportUpdateContainer<LineCreateCsvModel>> bulkImportCreateContainers) {
    return executeBulkImport(bulkImportCreateContainers,
        lineBulkImportService::createLineByUsername,
        lineBulkImportService::createLine);
  }

  @Override
  public List<BulkImportItemExecutionResult> lineUpdate(
      List<BulkImportUpdateContainer<LineUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        lineBulkImportService::updateLineByUsername,
        lineBulkImportService::updateLine);
  }

}
