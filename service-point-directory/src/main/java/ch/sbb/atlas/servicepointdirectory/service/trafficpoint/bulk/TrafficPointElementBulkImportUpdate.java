package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk.GeolocationBulkImportDataMapper;

public class TrafficPointElementBulkImportUpdate extends
    GeolocationBulkImportDataMapper<TrafficPointUpdateCsvModel, TrafficPointElementVersion,
        CreateTrafficPointElementVersionModel> {

  public static CreateTrafficPointElementVersionModel apply(
      BulkImportUpdateContainer<TrafficPointUpdateCsvModel> bulkImportContainer,
      TrafficPointElementVersion currentVersion) {
    return new TrafficPointElementBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new CreateTrafficPointElementVersionModel());
  }

  @Override
  protected void applySpecificUpdate(TrafficPointUpdateCsvModel update, TrafficPointElementVersion currentVersion,
      CreateTrafficPointElementVersionModel updateModel) {

    setNonUpdatableValues(currentVersion, updateModel);

    updateModel.setTrafficPointElementGeolocation(
        applyGeolocationUpdate(currentVersion.getTrafficPointElementGeolocation(), update));
  }

  private static void setNonUpdatableValues(TrafficPointElementVersion currentVersion,
      CreateTrafficPointElementVersionModel updateModel) {
    updateModel.setId(currentVersion.getId());
    updateModel.setEtagVersion(currentVersion.getVersion());

    updateModel.setNumberWithoutCheckDigit(currentVersion.getServicePointNumber().getNumber());
    updateModel.setSloid(currentVersion.getSloid());
  }

}
