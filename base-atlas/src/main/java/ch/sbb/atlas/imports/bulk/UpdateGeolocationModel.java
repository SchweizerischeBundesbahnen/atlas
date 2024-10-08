package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.servicepoint.SpatialReference;

public interface UpdateGeolocationModel {
  Double getNorth();
  Double getEast();
  SpatialReference getSpatialReference();
  Double getHeight();
}

