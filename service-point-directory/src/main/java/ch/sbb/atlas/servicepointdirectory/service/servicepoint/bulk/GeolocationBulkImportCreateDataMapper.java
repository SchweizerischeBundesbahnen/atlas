package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportCreateDataMapper;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_LV_MAX_DIGITS;
import static ch.sbb.atlas.api.AtlasApiConstants.ATLAS_WGS84_MAX_DIGITS;

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
