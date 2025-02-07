package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk.GeolocationBulkImportCreateDataMapper;

public class TrafficPointElementBulkImportCreate extends
    GeolocationBulkImportCreateDataMapper<TrafficPointCreateCsvModel, CreateTrafficPointElementVersionModel> {

  public static CreateTrafficPointElementVersionModel apply(
      BulkImportUpdateContainer<TrafficPointCreateCsvModel> bulkImportContainer) {
    return new TrafficPointElementBulkImportCreate().applyCreate(bulkImportContainer,
        new CreateTrafficPointElementVersionModel());
  }

  @Override
  protected void applySpecificCreate(TrafficPointCreateCsvModel create,
      CreateTrafficPointElementVersionModel createModel) {
    createModel.setTrafficPointElementGeolocation(applyGeolocationUpdate(create));
    if (create.getNumber() != null) {
      createModel.setNumberWithoutCheckDigit(create.getNumber());
    }
    if (create.getStopPointSloid() != null) {
      createModel.setNumberWithoutCheckDigit(SloidHelper.getServicePointNumber(create.getStopPointSloid()).getNumber());
    }
  }
}
