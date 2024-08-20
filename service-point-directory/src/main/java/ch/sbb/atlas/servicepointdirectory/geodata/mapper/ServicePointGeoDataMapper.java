package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
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

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  private static Map<String, Object> getProperties(ServicePointGeoData geolocation) {
    Map<String, Object> properties = new HashMap<>();

    properties.put("id", geolocation.getId());
    properties.put("number", geolocation.getNumber());
    properties.put("type", geolocation.getServicePointType());
    properties.put("designationOfficial", geolocation.getDesignationOfficial());

    return properties;
  }

  public List<Point> mapToWgs84WebGeometry(Collection<ServicePointGeoData> geolocations) {
    return geolocations.stream().map(this::mapGeoDataToWgs84WebGeometry).toList();
  }

  public Point mapGeoDataToWgs84WebGeometry(ServicePointGeoData geoData) {
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

    return mapGeometry(coordinateWgsWeb.getEast(), coordinateWgsWeb.getNorth(), getProperties(geoData));
  }

  private Point mapGeometry(double east, double north, Map<String, Object> properties) {
    final Point point = geometryFactory.createPoint(new Coordinate(east, north));
    point.setSRID(SpatialReference.WGS84WEB.getWellKnownId());
    point.setUserData(properties);
    return point;
  }
}
