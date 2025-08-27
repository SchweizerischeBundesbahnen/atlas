package ch.sbb.importservice.module.bulkimport.job.sepodi.servicepoint.create;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import ch.sbb.importservice.module.bulkimport.service.BulkImportType;

public class ServicePointCreate implements BulkImportType {

  public static final BulkImportConfig CONFIG = BulkImportConfig.builder()
      .application(ApplicationType.SEPODI)
      .objectType(BusinessObjectType.SERVICE_POINT)
      .importType(ImportType.CREATE)
      .build();

  @Override
  public BulkImportConfig getBulkImportConfig() {
    return CONFIG;
  }
}
