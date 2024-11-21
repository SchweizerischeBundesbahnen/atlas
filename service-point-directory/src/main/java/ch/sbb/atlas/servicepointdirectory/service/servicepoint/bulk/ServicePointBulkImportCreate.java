package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;

public class ServicePointBulkImportCreate extends
        GeolocationBulkImportUpdateDataMapper<ServicePointCreateCsvModel, CreateServicePointVersionModel> {

    public static CreateServicePointVersionModel apply(BulkImportUpdateContainer<ServicePointCreateCsvModel> bulkImportContainer, )
}
