package ch.sbb.atlas.validation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import org.junit.jupiter.api.Test;

class ValidSectorSpatialReferenceValidatorTest {

  private final ValidSectorSpatialReference.Validator validator = new ValidSectorSpatialReference.Validator();

  @Test
  void shouldTreatNullModelAsValid() {
    assertThat(validator.isValid(null, null)).isTrue();
  }

  @Test
  void shouldTreatMissingSectorGeolocationAsValid() {
    CreateSectorVersionModel model = CreateSectorVersionModel.builder().build();

    assertThat(validator.isValid(model, null)).isTrue();
  }

  @Test
  void shouldAllowLv95() {
    assertThat(validator.isValid(modelWithSpatialReference(SpatialReference.LV95), null)).isTrue();
  }

  @Test
  void shouldAllowWgs84() {
    assertThat(validator.isValid(modelWithSpatialReference(SpatialReference.WGS84), null)).isTrue();
  }

  @Test
  void shouldRejectLv03() {
    assertThat(validator.isValid(modelWithSpatialReference(SpatialReference.LV03), null)).isFalse();
  }

  @Test
  void shouldRejectWgs84Web() {
    assertThat(validator.isValid(modelWithSpatialReference(SpatialReference.WGS84WEB), null)).isFalse();
  }

  private static CreateSectorVersionModel modelWithSpatialReference(SpatialReference spatialReference) {
    return CreateSectorVersionModel.builder()
        .sectorGeolocation(GeolocationBaseCreateModel.builder()
            .spatialReference(spatialReference)
            .build())
        .build();
  }
}
