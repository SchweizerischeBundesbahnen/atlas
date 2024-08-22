package ch.sbb.atlas.api.servicepoint;

public interface TransformableGeolocation {

  SpatialReference getSpatialReference();

  void setSpatialReference(SpatialReference spatialReference);

  Double getNorth();

  void setNorth(Double north);

  Double getEast();

  void setEast(Double east);

}
