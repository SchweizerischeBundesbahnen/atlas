package ch.sbb.atlas.servicepointdirectory.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
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

    assertThat(result.getNorth()).isEqualTo(6274861.394006577);
    assertThat(result.getEast()).isEqualTo(779236.435552915);
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

    assertThat(result.getNorth()).isEqualTo(1427959.1864304289);
    assertThat(result.getEast()).isEqualTo(2567886.754474996);
  }

  @Test
  void transformWGS84ToLV03() {
    CoordinatePair result = coordinateTransformer.transform(
        TEST_COORDINATE_WGS84, SpatialReference.LV03);

    assertThat(result.getNorth()).isEqualTo(427959.1864304288);
    assertThat(result.getEast()).isEqualTo(567886.7544749964);
  }
}
