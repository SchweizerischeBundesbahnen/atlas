package ch.sbb.atlas.servicepointdirectory.module.bulkimport.trafficpoint.service;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.mapper.GeolocationBulkImportUpdateDataMapper;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;

public class TrafficPointElementBulkImportUpdate extends
    GeolocationBulkImportUpdateDataMapper<TrafficPointUpdateCsvModel, TrafficPointElementVersion,
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
    updateModel.setTrafficPointElementGeolocation(
        applyGeolocationUpdate(currentVersion.getTrafficPointElementGeolocation(), update));
    updateModel.setTrafficPointElementType(currentVersion.getTrafficPointElementType());
  }

}
