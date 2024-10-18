package ch.sbb.atlas.servicepointdirectory.service;

import static ch.sbb.atlas.imports.util.BulkImportUtility.applyUpdateIfValueNotNull;

import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportGeolocationUtility {

  public static <G extends GeolocationBaseEntity, U extends UpdateGeolocationModel>
  boolean geolocationValuesAreNull(G geolocation, U update) {
    return geolocation == null &&
        update.getNorth() == null &&
        update.getEast() == null &&
        update.getSpatialReference() == null;
  }

  public static <G extends GeolocationBaseEntity, U extends UpdateGeolocationModel>
  void applyGeolocationUpdates(U update, G geolocation) {
    applyUpdateIfValueNotNull(update.getNorth(), geolocation::setNorth);
    applyUpdateIfValueNotNull(update.getEast(), geolocation::setEast);
    applyUpdateIfValueNotNull(update.getSpatialReference(), geolocation::setSpatialReference);
    applyUpdateIfValueNotNull(update.getHeight(), geolocation::setHeight);
  }

}
