package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.servicepoint.Country;

public class ServicePointBulkImportCreate extends
    GeolocationBulkImportCreateDataMapper<ServicePointCreateCsvModel, CreateServicePointVersionModel> {

  public static CreateServicePointVersionModel apply(BulkImportUpdateContainer<ServicePointCreateCsvModel> bulkImportContainer) {
    return new ServicePointBulkImportCreate().applyCreate(bulkImportContainer,
        new CreateServicePointVersionModel());
  }

  @Override
  protected void applySpecificCreate(ServicePointCreateCsvModel create,
      CreateServicePointVersionModel createModel) {
    createModel.setServicePointGeolocation(applyGeolocationUpdate(create));
    createModel.setCountry(Country.from(create.getUicCountryCode()));
  }
}
