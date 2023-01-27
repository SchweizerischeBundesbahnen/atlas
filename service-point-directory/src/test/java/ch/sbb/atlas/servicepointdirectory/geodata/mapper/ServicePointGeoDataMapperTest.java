package ch.sbb.atlas.servicepointdirectory.geodata.mapper;

import static ch.sbb.atlas.servicepointdirectory.model.TestData.testGeoDataLv95;
import static ch.sbb.atlas.servicepointdirectory.model.TestData.testGeoDataWgs84Web;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import ch.sbb.atlas.servicepointdirectory.transformer.CoordinateTransformer;
import java.util.List;
import java.util.Map;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

class ServicePointGeoDataMapperTest {

  private ServicePointGeoDataMapper servicePointGeoDataMapper;

  @BeforeEach
  void setUp() {
    servicePointGeoDataMapper = new ServicePointGeoDataMapper(new CoordinateTransformer());
  }

  @Test
  void canMapLv96Geometry() {
    final Offset<Double> doubleOffset = Offset.offset(0.00001);
    final ServicePointGeoData geoData = testGeoDataLv95();
    final Point pointWgs84Web = servicePointGeoDataMapper.mapGeoDataToWgs84WebGeometry(geoData);
    // Values from CSV-Export
    assertThat(pointWgs84Web.getX()).isCloseTo(937373.39333, doubleOffset);
    assertThat(pointWgs84Web.getY()).isCloseTo(5998919.02171, doubleOffset);
    assertThat(pointWgs84Web.getUserData()).isNotNull();
    assertThat(((Map<String, Object>) pointWgs84Web.getUserData()).size()).isGreaterThanOrEqualTo(
        1);
  }

  @Test
  void mapToGeometryItem() {
    final ServicePointGeoData geoData = testGeoDataWgs84Web();
    final Point point = servicePointGeoDataMapper.mapGeoDataToWgs84WebGeometry(geoData);
    assertThat(point.getX()).isEqualTo(0.1D);
    assertThat(point.getY()).isEqualTo(0.1D);
    assertThat(point.getUserData()).isNotNull();
    assertThat(((Map<String, Object>) point.getUserData()).size()).isGreaterThanOrEqualTo(1);
  }

  @Test
  void mapToGeometryList() {
    final ServicePointGeoData geoData = testGeoDataWgs84Web();
    final List<Point> points = servicePointGeoDataMapper.mapToWgs84WebGeometry(List.of(geoData));
    assertThat(points).isNotEmpty();
  }

  @Test
  void mapToGeometryListIfEmpty() {
    final List<Point> points = servicePointGeoDataMapper.mapToWgs84WebGeometry(List.of());
    assertThat(points).isEmpty();
  }
}
