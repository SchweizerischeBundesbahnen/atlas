package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sectorgroup.service;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportCreateDataMapper;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel;

public class SectorGroupBulkImportCreate extends
    BulkImportCreateDataMapper<SectorGroupCreateCsvModel, CreateSectorGroupVersionModel> {

  public static CreateSectorGroupVersionModel apply(
      BulkImportUpdateContainer<SectorGroupCreateCsvModel> bulkImportContainer) {
    return new SectorGroupBulkImportCreate().applyCreate(bulkImportContainer,
        new CreateSectorGroupVersionModel());
  }

}
