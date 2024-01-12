package ch.sbb.atlas.servicepoint.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.DoubleAssertion;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import org.junit.jupiter.api.Test;

class CoordinateTransformerTest {

  private static final CoordinatePair TEST_COORDINATE_WGS84 = CoordinatePair
      .builder()
      .east(7.0)
      .north(49.0)
      .spatialReference(SpatialReference.WGS84)
      .build();

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();

  @Test
  void transformWGS84ToWGS84WEB() {
    CoordinatePair result = coordinateTransformer.transform(
        TEST_COORDINATE_WGS84, SpatialReference.WGS84WEB);

    assertThat(result.getNorth()).isCloseTo(6274861.3940065, DoubleAssertion.equalOnDecimalDigits(4));
    assertThat(result.getEast()).isCloseTo(779236.43555291, DoubleAssertion.equalOnDecimalDigits(4));
  }

  @Test
  void transformSame() {
    CoordinatePair result = coordinateTransformer.transform(
        TEST_COORDINATE_WGS84, SpatialReference.WGS84);

    assertThat(result.getNorth()).isEqualTo(49D);
    assertThat(result.getEast()).isEqualTo(7D);
  }

  @Test
  void transformWGS84ToLV95() {
    CoordinatePair result = coordinateTransformer.transform(
        TEST_COORDINATE_WGS84, SpatialReference.LV95);

    assertThat(result.getNorth()).isEqualTo(1427959.18643);
    assertThat(result.getEast()).isEqualTo(2567886.75447);
  }

  @Test
  void transformWGS84ToLV03() {
    CoordinatePair result = coordinateTransformer.transform(
        TEST_COORDINATE_WGS84, SpatialReference.LV03);

    assertThat(result.getNorth()).isEqualTo(427959.18643);
    assertThat(result.getEast()).isEqualTo(567886.75447);
  }
}
