package ch.sbb.atlas.servicepointdirectory.service.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class GeolocationMapperUtilTest {

  @Test
  void getOriginalNorthDoesNotThrowException() {
    assertDoesNotThrow(() -> Stream
        .of(SpatialReference.values())
        .forEach(sr -> GeolocationMapperUtil
            .getOriginalNorth(sr, 0D, 0D, 0D, 0D)));
  }

  @Test
  void getOriginalKnowsWgs84RangeMax() {
    Double north = GeolocationMapperUtil
        .getOriginalNorth(SpatialReference.WGS84, 1D, 2D, 3D, 4D);
    assertThat(north).isEqualTo(1D);
  }

  @Test
  void getOriginalNorthKnowsOriginWgs84Web() {
    Double north = GeolocationMapperUtil
        .getOriginalNorth(SpatialReference.WGS84WEB, 1D, 2D, 3D, 4D);
    assertThat(north).isEqualTo(2D);
  }

  @Test
  void getOriginalNorthKnowsOriginLv95() {
    Double north = GeolocationMapperUtil
        .getOriginalNorth(SpatialReference.LV95, 1D, 2D, 3D, 4D);
    assertThat(north).isEqualTo(3D);
  }

  @Test
  void getOriginalNorthKnowsOriginLv03() {
    Double north = GeolocationMapperUtil
        .getOriginalNorth(SpatialReference.LV03, 1D, 2D, 3D, 4D);
    assertThat(north).isEqualTo(4D);
  }

  @Test
  void getOriginalNorthKnowsWgs84Rangeinx() {
    Double north = GeolocationMapperUtil
        .getOriginalNorth(SpatialReference.WGS84, -90.1, 0D, 0D, 0D);
    assertThat(north).isNull();
  }

  @Test
  void getOriginalNorthKnowsWgs84RangeMax() {
    Double north = GeolocationMapperUtil
        .getOriginalNorth(SpatialReference.WGS84, 90.1, 0D, 0D, 0D);
    assertThat(north).isNull();
  }

  @Test
  void getOriginalEastDoesNotThrowException() {
    assertDoesNotThrow(() -> Stream
        .of(SpatialReference.values())
        .forEach(sr -> GeolocationMapperUtil
            .getOriginalEast(sr, 0D, 0D, 0D, 0D)));
  }

  @Test
  void getOriginalEastKnowsOriginWgs84() {
    Double east = GeolocationMapperUtil
        .getOriginalEast(SpatialReference.WGS84, 1D, 2D, 3D, 4D);
    assertThat(east).isEqualTo(1D);
  }

  @Test
  void getOriginalEastKnowsOriginWgs84Web() {
    Double east = GeolocationMapperUtil
        .getOriginalEast(SpatialReference.WGS84WEB, 1D, 2D, 3D, 4D);
    assertThat(east).isEqualTo(2D);
  }

  @Test
  void getOriginalEastKnowsOriginLv95() {
    Double east = GeolocationMapperUtil
        .getOriginalEast(SpatialReference.LV95, 1D, 2D, 3D, 4D);
    assertThat(east).isEqualTo(3D);
  }

  @Test
  void getOriginalEastKnowsOriginLv03() {
    Double east = GeolocationMapperUtil
        .getOriginalEast(SpatialReference.LV03, 1D, 2D, 3D, 4D);
    assertThat(east).isEqualTo(4D);
  }

  @Test
  void getOriginalEastKnowsWgs84RangeMin() {
    Double east = GeolocationMapperUtil
        .getOriginalEast(SpatialReference.WGS84, -180.1, 0D, 0D, 0D);
    assertThat(east).isNull();
  }

  @Test
  void getOriginalEastKnowsWgs84RangeMax() {
    Double east = GeolocationMapperUtil
        .getOriginalEast(SpatialReference.WGS84, 180.1, 0D, 0D, 0D);
    assertThat(east).isNull();
  }
}
