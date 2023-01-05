package ch.sbb.atlas.servicepointdirectory.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpatialReferenceTransformerTest {

  private static final CoordinatePair TEST_COORDINATE_WGS84 =
      CoordinatePair.builder().east(7.0).north(49.0).build();
  private SpatialReferenceTransformer spatialReferenceTransformer;

  @BeforeEach
  void initialize() {
    spatialReferenceTransformer = new SpatialReferenceTransformer();
  }

  @Test
  void transformWGS84ToWGS84WEB() {
    CoordinatePair result = spatialReferenceTransformer.transform(SpatialReference.WGS84,
        SpatialReference.WGS84WEB, TEST_COORDINATE_WGS84);

    assertThat(result.getNorth()).isEqualTo(6274861.394006577);
    assertThat(result.getEast()).isEqualTo(779236.435552915);
  }

  @Test
  void transformWGS84ToLV95() {
    CoordinatePair result = spatialReferenceTransformer.transform(SpatialReference.WGS84,
        SpatialReference.LV95, TEST_COORDINATE_WGS84);

    assertThat(result.getNorth()).isEqualTo(1427959.1864304289);
    assertThat(result.getEast()).isEqualTo(2567886.754474996);
  }

  @Test
  void transformWGS84ToLV03() {
    CoordinatePair result = spatialReferenceTransformer.transform(SpatialReference.WGS84,
        SpatialReference.LV03, TEST_COORDINATE_WGS84);

    assertThat(result.getNorth()).isEqualTo(427959.1864304288);
    assertThat(result.getEast()).isEqualTo(567886.7544749964);
  }
}
