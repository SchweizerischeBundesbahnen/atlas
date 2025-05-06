package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.TerminateServicePointModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;

public class ServicePointBulkImportTerminate extends BulkImportUpdateDataMapper<ServicePointTerminateCsvModel,
    ServicePointVersion, TerminateServicePointModel> {

  public static TerminateServicePointModel apply(BulkImportUpdateContainer<ServicePointTerminateCsvModel> bulkImportContainer,
      ServicePointVersion currentVersion) {
    return new ServicePointBulkImportTerminate().applyUpdate(bulkImportContainer, currentVersion,
        new TerminateServicePointModel());
  }

}
