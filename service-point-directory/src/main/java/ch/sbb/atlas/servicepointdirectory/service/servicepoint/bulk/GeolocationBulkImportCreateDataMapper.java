package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportCreateDataMapper;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;

import static ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk.GeolocationBulkImportUpdateDataMapper.roundToSpatialReferencePrecision;

public abstract class GeolocationBulkImportCreateDataMapper<T, V> extends BulkImportCreateDataMapper<T, V> {

  protected static GeolocationBaseCreateModel applyGeolocationUpdate(UpdateGeolocationModel update) {
    // If currently null and import is not adding, keep it null
    if (update.getNorth() == null &&
        update.getEast() == null &&
        update.getSpatialReference() == null) {
      return null;
    }

    return buildGeolocation(update);
  }

  private static GeolocationBaseCreateModel buildGeolocation(UpdateGeolocationModel update) {
    GeolocationBaseCreateModel geolocationModel = new GeolocationBaseCreateModel();

    SpatialReference spatialReference = update.getSpatialReference();
    geolocationModel.setSpatialReference(spatialReference);

    geolocationModel.setNorth(roundToSpatialReferencePrecision(update.getNorth(), spatialReference));
    geolocationModel.setEast(roundToSpatialReferencePrecision(update.getEast(), spatialReference));

    geolocationModel.setHeight(update.getHeight());

    return geolocationModel;
  }

}
