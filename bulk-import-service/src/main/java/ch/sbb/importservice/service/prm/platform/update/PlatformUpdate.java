package ch.sbb.importservice.service.prm.platform.update;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.service.bulk.BulkImportType;

public abstract class PlatformUpdate implements BulkImportType {

  public static final BulkImportConfig CONFIG = BulkImportConfig.builder()
      .application(ApplicationType.PRM)
      .objectType(BusinessObjectType.PLATFORM_REDUCED)
      .importType(ImportType.UPDATE)
      .build();

  @Override
  public BulkImportConfig getBulkImportConfig() {
    return CONFIG;
  }

}
