package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.servicepoint.CoordinatePair;
import lombok.Data;

@Data
public class GeoAdminParams {

  private final Integer sr;
  private final String geometry;
  private final String layers;

  private final String geometryType;
  private final String imageDisplay;
  private final String mapExtent;
  private final Integer tolerance;
  private final boolean returnGeometry;

  public GeoAdminParams(CoordinatePair coordinatePair) {
    this.sr = coordinatePair.getSpatialReference().getWellKnownId();
    this.geometry = coordinatePair.getEast() + "," + coordinatePair.getNorth();
    this.layers = Layers.getLayersParam();
    geometryType = "esriGeometryPoint";
    imageDisplay = "0,0,0";
    mapExtent = "0,0,0,0";
    tolerance = 0;
    returnGeometry = false;
  }
}