package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.controller;

import ch.sbb.atlas.api.servicepoint.SectorGroupBulkImportApi;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service.SectorGroupBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SectorGroupBulkImportController extends BaseBulkImportControllerInternal implements
    SectorGroupBulkImportApi {

  private final SectorGroupBulkImportService sectorGroupBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportCreate(
      List<BulkImportUpdateContainer<SectorGroupCreateCsvModel>> bulkImportCreateContainers) {
    return executeBulkImport(bulkImportCreateContainers,
        sectorGroupBulkImportService::createSectorGroupByUserName,
        sectorGroupBulkImportService::createSectorGroup);
  }

}
