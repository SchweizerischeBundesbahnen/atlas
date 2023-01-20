package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinateTransformer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicePointGeoDataMapper {

  private final GeometryFactory geometryFactory = new GeometryFactory();

  private final CoordinateTransformer coordinateTransformer;

  /***
   * Map geo-data geometries to a list of WGS84WEB points.
   */
  public List<Point> mapToGeometryList(Collection<ServicePointGeoData> geolocations) {
    if (isEmpty(geolocations)) {
      return List.of();
    }
    return geolocations.stream().map(this::mapGeoDataToGeometry).toList();
  }

  /***
   * Map geo-data geometry to a WGS84WEB point.
   */
  public Point mapGeoDataToGeometry(ServicePointGeoData geoData) {
    CoordinatePair coordinateWgsWeb = CoordinatePair
        .builder()
        .spatialReference(geoData.getSpatialReference())
        .north(geoData.getNorth())
        .east(geoData.getEast())
        .build();

    if (geoData.getSpatialReference() != SpatialReference.WGS84WEB) {
      coordinateWgsWeb = coordinateTransformer.transform(CoordinatePair
              .builder()
              .spatialReference(geoData.getSpatialReference())
              .north(geoData.getNorth())
              .east(geoData.getEast())
              .build(),
          SpatialReference.WGS84WEB);
    }

    return mapGeometry(coordinateWgsWeb.getEast(), coordinateWgsWeb.getNorth(),
        getProperties(geoData));
  }

  private Point mapGeometry(double east, double north, Map<String, Object> properties) {
    final Point point = geometryFactory.createPoint(new Coordinate(east, north));
    point.setSRID(SpatialReference.WGS84WEB.getWellKnownId());
    point.setUserData(properties);
    return point;
  }

  private static Map<String, Object> getProperties(ServicePointGeoData geolocation) {
    return new HashMap<>() {{
      put("id", geolocation.getId());
      put("number", geolocation.getNumber());
    }};
  }
}
