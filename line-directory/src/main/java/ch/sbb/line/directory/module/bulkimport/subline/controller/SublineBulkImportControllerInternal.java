package ch.sbb.line.directory.module.bulkimport.subline.controller;

import ch.sbb.atlas.api.lidi.SublineBulkImportApiInternal;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.line.directory.module.bulkimport.subline.service.SublineBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SublineBulkImportControllerInternal extends BaseBulkImportControllerInternal implements SublineBulkImportApiInternal {

  private final SublineBulkImportService sublineBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> sublineUpdate(
      List<BulkImportUpdateContainer<SublineUpdateCsvModel>> bulkImportContainers) {
    return executeBulkImport(bulkImportContainers,
        sublineBulkImportService::updateSublineByUsername,
        sublineBulkImportService::updateSubline);
  }

}

