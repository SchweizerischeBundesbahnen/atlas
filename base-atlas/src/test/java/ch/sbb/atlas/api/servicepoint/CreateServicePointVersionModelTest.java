package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CreateServicePointVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAllowMinimalServicePointVersion() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithMissingNorth() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationCreateModel.builder()
            .spatialReference(SpatialReference.LV95)
            .east(4.64654)
            .build())
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldAllowMinimalServicePointVersionWithLv95Coordinates() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationCreateModel.builder()
            .spatialReference(SpatialReference.LV95)
            .east(2600783.0)
            .north(1201099.0)
            .height(555.0)
            .build())
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithLv95CoordinatesMoreThanFiveFractions() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationCreateModel.builder()
            .spatialReference(SpatialReference.LV95)
            .east(2600783.0654654)
            .north(1201099.0)
            .height(555.0)
            .build())
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldAllowMinimalServicePointVersionWithWgs84Coordinates() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationCreateModel.builder()
            .spatialReference(SpatialReference.WGS84)
            .east(7.44030833981)
            .north(46.94907577445)
            .height(555.0)
            .build())
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithWgs84CoordinatesMoreThanElevenFractions() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationCreateModel.builder()
            .spatialReference(SpatialReference.WGS84)
            .east(7.44030833981123)
            .north(46.94907577445123)
            .height(555.0)
            .build())
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }
}