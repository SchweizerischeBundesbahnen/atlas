package ch.sbb.atlas.servicepointdirectory.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoordinateTransformerTest {

  private static final CoordinatePair TEST_COORDINATE_WGS84 = CoordinatePair
      .builder()
      .east(7.0)
      .north(49.0)
      .spatialReference(SpatialReference.WGS84)
      .build();
  public static final int TOTAL_SERVICE_POINTS_WITH_GEOLOCATION = 100000;
  private CoordinateTransformer coordinateTransformer;

  @BeforeEach
  void initialize() {
    coordinateTransformer = new CoordinateTransformer();
  }

  @Test
  void transformWGS84ToWGS84WEB() {
    CoordinatePair result = coordinateTransformer.transform(
        TEST_COORDINATE_WGS84, SpatialReference.WGS84WEB);

    assertThat(result.getNorth()).isEqualTo(6274861.394006577);
    assertThat(result.getEast()).isEqualTo(779236.435552915);
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

  @Test
  void transformPerformance() {
    CoordinatePair testCoordinatesWgs84;
    long start = System.nanoTime();

    for (int i = 0; i < TOTAL_SERVICE_POINTS_WITH_GEOLOCATION; i++) {
      final double moveBy = (i * 0.00001);
      testCoordinatesWgs84 = CoordinatePair
          .builder()
          .east(7.0 + moveBy)
          .north(45.0 + moveBy)
          .spatialReference(SpatialReference.WGS84)
          .build();
      coordinateTransformer.transform(testCoordinatesWgs84, SpatialReference.WGS84WEB);
      coordinateTransformer.transform(testCoordinatesWgs84, SpatialReference.LV95);
      coordinateTransformer.transform(testCoordinatesWgs84, SpatialReference.LV03);
    }

    final double elapsedMs = (System.nanoTime() - start) / 1000_000;
    System.out.println(elapsedMs);
    assertThat(elapsedMs).isLessThan(1000);
  }

}
