package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_LV_MAX_DIGITS;
import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_WGS84_MAX_DIGITS;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public abstract class GeolocationBulkImportUpdateDataMapper<T, U, V> extends BulkImportUpdateDataMapper<T, U, V> {

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

    SpatialReference spatialReference = Optional.ofNullable(update.getSpatialReference())
        .orElse(currentGeolocation == null ? null : currentGeolocation.getSpatialReference());
    geolocationModel.setSpatialReference(spatialReference);

    geolocationModel.setNorth(Optional.ofNullable(roundToSpatialReferencePrecision(update.getNorth(), spatialReference))
        .orElse(currentGeolocation == null ? null : currentGeolocation.getNorth()));
    geolocationModel.setEast(Optional.ofNullable(roundToSpatialReferencePrecision(update.getEast(), spatialReference))
        .orElse(currentGeolocation == null ? null : currentGeolocation.getEast()));
    geolocationModel.setHeight(
        Optional.ofNullable(update.getHeight()).orElse(currentGeolocation == null ? null : currentGeolocation.getHeight()));

    return geolocationModel;
  }

  public static Double roundToSpatialReferencePrecision(Double value, SpatialReference spatialReference) {
    if (value == null) {
      return null;
    }

    int newScale = spatialReference == SpatialReference.LV95 ? ATLAS_LV_MAX_DIGITS : ATLAS_WGS84_MAX_DIGITS;
    return BigDecimal.valueOf(value)
        .setScale(newScale, RoundingMode.HALF_UP)
        .doubleValue();
  }
}
