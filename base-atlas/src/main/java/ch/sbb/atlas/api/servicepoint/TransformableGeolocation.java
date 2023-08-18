package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;

public interface TransformableGeolocation {

  SpatialReference getSpatialReference();

  void setSpatialReference(SpatialReference spatialReference);

  Double getNorth();

  void setNorth(Double north);

  Double getEast();

  void setEast(Double east);

}
