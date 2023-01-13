package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class ServicePointGeoDataMapper {

  private final GeometryFactory geometryFactory;

  public ServicePointGeoDataMapper() {
    geometryFactory = new GeometryFactory();
  }

  public List<Point> mapToGeometryList(Collection<ServicePointVersion> geolocations) {
    if (isEmpty(geolocations)) {
      return List.of();
    }
    return geolocations.stream().map(this::mapToGeometry).toList();
  }

  public Point mapToGeometry(ServicePointVersion source) {
    return mapGeometry(source.getServicePointGeolocation().getEast(),
        source.getServicePointGeolocation().getNorth(), getProperties(source));
  }

  private Point mapGeometry(double east, double north, Map<String, Object> properties) {
    final Point point = geometryFactory.createPoint(new Coordinate(east, north));
    point.setSRID(SpatialReference.WGS84WEB.getWellKnownId());
    point.setUserData(properties);
    return point;
  }

  private static Map<String, Object> getProperties(ServicePointVersion geolocation) {
    return new HashMap<>() {{
      put("id", geolocation.getId());
      put("name", geolocation.getDesignationOfficial());
      put("number", geolocation.getNumber());
    }};
  }
}
