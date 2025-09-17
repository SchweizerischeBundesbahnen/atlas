package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.CoordinatePair;

public interface TransformableGeolocation {

  SpatialReference getSpatialReference();

  void setSpatialReference(SpatialReference spatialReference);

  Double getNorth();

  void setNorth(Double north);

  Double getEast();

  void setEast(Double east);

  default CoordinatePair asCoordinatePair() {
    return CoordinatePair.builder()
        .east(getEast())
        .north(getNorth())
        .spatialReference(getSpatialReference())
        .build();
  }

}
