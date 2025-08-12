package ch.sbb.atlas.api.servicepoint.sector;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectorVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldProvideAdditionalInformationCorrectly() {
    SectorVersionModel sectorVersionModel = SectorVersionModel.builder()
        .trafficPointSloid("cde")
        .north(11.11)
        .east(11.22)
        .spatialReference(SpatialReference.LV95)
        .designation("Bern")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<SectorVersionModel>> constraintViolations = validator.validate(sectorVersionModel);
    assertThat(constraintViolations).isEmpty();

  }

  @Test
  void shouldThrowWhenSpatialReferenceIsNotWGS84OrLV95() {
    // Given
    SectorVersionModel model = SectorVersionModel.builder()
        .trafficPointSloid("nonexistent-sloid")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusDays(1))
        .designation("foo")
        .length(1.0).north(0.0).east(0.0)
        .spatialReference(SpatialReference.LV03)
        .height(1.0).edgeHeight(1.0)
        .build();

    Set<ConstraintViolation<SectorVersionModel>> constraintViolations = validator.validate(model);

    assertThat(constraintViolations).isNotEmpty();

  }

}
