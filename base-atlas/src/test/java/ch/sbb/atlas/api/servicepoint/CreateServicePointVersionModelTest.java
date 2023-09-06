package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
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
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();

    assertThat(servicePointVersionModel.isRawServicePoint()).isTrue();
    assertThat(servicePointVersionModel.isOperatingPoint()).isFalse();
    assertThat(servicePointVersionModel.isOperatingPointWithTimetable()).isFalse();
    assertThat(servicePointVersionModel.isStopPoint()).isFalse();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithMissingNorth() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
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

  @Test
  void shouldNotAllowServiceWithOperatingPointTypeAndTechnical() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.OPERATING_POINT_BUS)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAllowServiceWithOperatingPointTypeAndTariffPoint() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAllowServiceWithPlainOperatingPointAndStopPoint() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .meansOfTransport(List.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAllowServiceWithPlainOperatingPointAndFreightServicePoint() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8507000)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .freightServicePoint(true)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }
}