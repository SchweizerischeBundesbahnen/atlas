package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.CoordinatePair;
import org.junit.jupiter.api.Test;

class TransformableGeolocationTest {

  @Test
  void shouldGetCoordinatePair() {
    TransformableGeolocation transformableGeolocation = GeolocationBaseCreateModel.builder()
        .north(10.5)
        .east(5.2)
        .spatialReference(SpatialReference.WGS84)
        .build();

    CoordinatePair expected = CoordinatePair.builder()
        .north(10.5)
        .east(5.2)
        .spatialReference(SpatialReference.WGS84)
        .build();
    assertThat(transformableGeolocation.asCoordinatePair()).isEqualTo(expected);
  }
}