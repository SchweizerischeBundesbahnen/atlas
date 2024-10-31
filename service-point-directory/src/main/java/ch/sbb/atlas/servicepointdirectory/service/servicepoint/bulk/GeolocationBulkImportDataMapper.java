package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.BulkImportDataMapper;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;

public abstract class GeolocationBulkImportDataMapper extends BulkImportDataMapper {

  protected static GeolocationBaseCreateModel applyGeolocationUpdate(GeolocationBaseEntity currentGeolocation,
      UpdateGeolocationModel update) {
    // If currently null and import is not adding, keep it null
    if (currentGeolocation == null &&
        update.getNorth() == null &&
        update.getEast() == null &&
        update.getSpatialReference() == null) {
      return null;
    }

    return buildGeolocation(currentGeolocation, update);
  }

  private static GeolocationBaseCreateModel buildGeolocation(GeolocationBaseEntity currentGeolocation,
      UpdateGeolocationModel update) {
    GeolocationBaseCreateModel geolocationModel = new GeolocationBaseCreateModel();

    applyValueWithDefault(update.getNorth(), currentGeolocation == null ? null : currentGeolocation.getNorth(),
        geolocationModel::setNorth);
    applyValueWithDefault(update.getEast(), currentGeolocation == null ? null : currentGeolocation.getEast(),
        geolocationModel::setEast);
    applyValueWithDefault(update.getSpatialReference(),
        currentGeolocation == null ? null : currentGeolocation.getSpatialReference(), geolocationModel::setSpatialReference);

    applyValueWithDefault(update.getHeight(), currentGeolocation == null ? null : currentGeolocation.getHeight(),
        geolocationModel::setHeight);

    return geolocationModel;
  }
}
