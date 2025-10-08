package ch.sbb.importservice.module.bulkimport.job.sepodi.trafficpoint.terminate;

import ch.sbb.atlas.imports.bulk.model.BusinessObjectType;
import ch.sbb.atlas.imports.bulk.model.ImportType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import ch.sbb.importservice.module.bulkimport.service.BulkImportType;

public abstract class TrafficPointTerminate implements BulkImportType {

  public static final BulkImportConfig CONFIG = BulkImportConfig.builder()
      .application(ApplicationType.SEPODI)
      .objectType(BusinessObjectType.TRAFFIC_POINT)
      .importType(ImportType.TERMINATE)
      .build();

  @Override
  public BulkImportConfig getBulkImportConfig() {
    return CONFIG;
  }

}
