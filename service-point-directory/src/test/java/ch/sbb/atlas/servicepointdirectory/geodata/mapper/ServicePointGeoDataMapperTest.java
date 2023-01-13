package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static ch.sbb.atlas.servicepointdirectory.model.TestData.testGeolocation;
import static ch.sbb.atlas.servicepointdirectory.model.TestData.testServicePoint;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

class ServicePointGeoDataMapperTest {

  ServicePointGeoDataMapper servicePointGeoDataMapper;

  @BeforeEach
  void setUp() {
    servicePointGeoDataMapper = new ServicePointGeoDataMapper();
  }

  @Test
  void mapToGeometryItem() {
    final ServicePointVersion servicePointVersion = testServicePoint();
    servicePointVersion.setServicePointGeolocation(testGeolocation());

    final Point point = servicePointGeoDataMapper.mapToGeometry(servicePointVersion);
    assertThat(point.getX()).isEqualTo(0.1D);
    assertThat(point.getY()).isEqualTo(0.1D);
    assertThat(point.getUserData()).isNotNull();
    assertThat(((Map<String, Object>) point.getUserData()).size()).isEqualTo(3);
  }

  @Test
  void mapToGeometryList() {
    final ServicePointVersion servicePointVersion = testServicePoint();
    servicePointVersion.setServicePointGeolocation(testGeolocation());

    final List<Point> points = servicePointGeoDataMapper.mapToGeometryList(
        List.of(servicePointVersion));
    assertThat(points).isNotEmpty();
  }

  @Test
  void mapToGeometryListIfNull() {
    final List<Point> points = servicePointGeoDataMapper.mapToGeometryList(null);
    assertThat(points).isEmpty();
  }

  @Test
  void mapToGeometryListIfEmpty() {
    final List<Point> points = servicePointGeoDataMapper.mapToGeometryList(List.of());
    assertThat(points).isEmpty();
  }
}
