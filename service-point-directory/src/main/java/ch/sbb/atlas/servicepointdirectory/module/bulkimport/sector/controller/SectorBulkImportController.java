package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.controller;

import ch.sbb.atlas.api.servicepoint.SectorBulkImportApi;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BaseBulkImportControllerInternal;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.service.SectorBulkImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SectorBulkImportController extends BaseBulkImportControllerInternal implements
    SectorBulkImportApi {

  private final SectorBulkImportService sectorBulkImportService;

  @Override
  public List<BulkImportItemExecutionResult> bulkImportCreate(
      List<BulkImportUpdateContainer<SectorCreateCsvModel>> bulkImportCreateContainers) {
    return executeBulkImport(bulkImportCreateContainers,
        sectorBulkImportService::createSectorByUserName,
        sectorBulkImportService::createSector);
  }

}
