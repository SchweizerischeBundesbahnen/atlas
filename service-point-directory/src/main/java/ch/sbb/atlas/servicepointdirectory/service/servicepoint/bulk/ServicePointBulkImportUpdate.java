package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;

public class ServicePointBulkImportUpdate extends
        GeolocationBulkImportUpdateDataMapper<ServicePointUpdateCsvModel, ServicePointVersion, UpdateServicePointVersionModel> {

  public static UpdateServicePointVersionModel apply(BulkImportUpdateContainer<ServicePointUpdateCsvModel> bulkImportContainer,
      ServicePointVersion currentVersion) {
    return new ServicePointBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new UpdateServicePointVersionModel());
  }

  @Override
  protected void applySpecificUpdate(ServicePointUpdateCsvModel update, ServicePointVersion currentVersion,
      UpdateServicePointVersionModel updateModel) {
    updateModel.setServicePointGeolocation(applyGeolocationUpdate(currentVersion.getServicePointGeolocation(), update));
  }

}
