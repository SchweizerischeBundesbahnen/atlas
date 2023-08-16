package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
public abstract class BasePointVersion<T extends BasePointVersion<T>> extends BaseDidokImportEntity {

  public boolean hasGeolocation() {
    throw new IllegalStateException("cannot have geolocation");
  }

  public void referenceGeolocationTo(T version) {
    throw new IllegalStateException("cannot have geolocation");
  }

  public GeolocationBaseEntity geolocation() {
    throw new IllegalStateException("cannot have geolocation");
  }

}
