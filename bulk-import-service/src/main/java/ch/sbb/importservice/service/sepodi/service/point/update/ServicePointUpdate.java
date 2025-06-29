package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.service.bulk.BulkImportType;

public abstract class ServicePointUpdate implements BulkImportType {

  public static final BulkImportConfig CONFIG = BulkImportConfig.builder()
      .application(ApplicationType.SEPODI)
      .objectType(BusinessObjectType.SERVICE_POINT)
      .importType(ImportType.UPDATE)
      .build();

  @Override
  public BulkImportConfig getBulkImportConfig() {
    return CONFIG;
  }
}
