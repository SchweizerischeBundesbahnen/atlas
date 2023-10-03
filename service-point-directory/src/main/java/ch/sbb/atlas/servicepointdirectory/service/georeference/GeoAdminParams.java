package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.servicepoint.CoordinatePair;
import lombok.Data;

@Data
public class GeoAdminParams {

    private final Integer sr;
    private final String geometry;
    private final String layers;

    private final String geometryType = "esriGeometryPoint";
    private final String imageDisplay = "0,0,0";
    private final String mapExtent = "0,0,0,0";
    private final Integer tolerance = 0;
    private final boolean returnGeometry = false;

    public GeoAdminParams(CoordinatePair coordinatePair) {
        this.sr = coordinatePair.getSpatialReference().getWellKnownId();
        this.geometry = coordinatePair.getEast() + "," + coordinatePair.getNorth();
        this.layers = Layers.getLayersParam();
    }
}