package ch.sbb.importservice.module.bulkimport.job.sepodi.sector.update;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import ch.sbb.importservice.module.bulkimport.service.BulkImportType;

public abstract class SectorUpdate implements BulkImportType {

  public static final BulkImportConfig CONFIG = BulkImportConfig.builder()
      .application(ApplicationType.SEPODI)
      .objectType(BusinessObjectType.SECTOR)
      .importType(ImportType.UPDATE)
      .build();

  @Override
  public BulkImportConfig getBulkImportConfig() {
    return CONFIG;
  }

}
