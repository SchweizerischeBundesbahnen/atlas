package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_LV_MAX_DIGITS;
import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_WGS84_MAX_DIGITS;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportDataMapper;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

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

    SpatialReference spatialReference = Optional.ofNullable(update.getSpatialReference())
        .orElse(currentGeolocation == null ? null : currentGeolocation.getSpatialReference());
    geolocationModel.setSpatialReference(spatialReference);

    applyValueWithDefault(roundToSpatialReferencePrecision(update.getNorth(), spatialReference),
        currentGeolocation == null ? null : currentGeolocation.getNorth(), geolocationModel::setNorth);
    applyValueWithDefault(roundToSpatialReferencePrecision(update.getEast(), spatialReference),
        currentGeolocation == null ? null : currentGeolocation.getEast(), geolocationModel::setEast);

    applyValueWithDefault(update.getHeight(), currentGeolocation == null ? null : currentGeolocation.getHeight(),
        geolocationModel::setHeight);

    return geolocationModel;
  }

  private static Double roundToSpatialReferencePrecision(Double value, SpatialReference spatialReference) {
    if (value == null) {
      return null;
    }

    int newScale = spatialReference == SpatialReference.LV95 ? ATLAS_LV_MAX_DIGITS : ATLAS_WGS84_MAX_DIGITS;
    return BigDecimal.valueOf(value)
        .setScale(newScale, RoundingMode.HALF_UP)
        .doubleValue();
  }
}
