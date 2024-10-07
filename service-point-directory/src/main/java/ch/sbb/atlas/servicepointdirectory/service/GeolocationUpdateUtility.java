package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GeolocationUpdateUtility {

  public static <G extends GeolocationBaseEntity, U extends UpdateGeolocationModel>
  boolean geolocationValuesAreNull(G geolocation, U update) {
    return geolocation == null &&
        update.getNorth() == null &&
        update.getEast() == null &&
        update.getSpatialReference() == null;
  }

  public static <T> void applyUpdateIfValueNotNull(T value, Consumer<T> setterFunction) {
    if (value != null) {
      setterFunction.accept(value);
    }
  }

  public static <G extends GeolocationBaseEntity, U extends UpdateGeolocationModel>
  void applyGeolocationUpdates(U update, G geolocation) {
    applyUpdateIfValueNotNull(update.getNorth(), geolocation::setNorth);
    applyUpdateIfValueNotNull(update.getEast(), geolocation::setEast);
    applyUpdateIfValueNotNull(update.getSpatialReference(), geolocation::setSpatialReference);
    applyUpdateIfValueNotNull(update.getHeight(), geolocation::setHeight);
  }

}
