package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.Country;
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

class UpdateServicePointVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAllowMinimalServicePointVersion() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();

    assertThat(servicePointVersionModel.isRawServicePoint()).isTrue();
    assertThat(servicePointVersionModel.isOperatingPoint()).isFalse();
    assertThat(servicePointVersionModel.isOperatingPointWithTimetable()).isFalse();
    assertThat(servicePointVersionModel.isStopPoint()).isFalse();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithMissingNorth() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .servicePointGeolocation(ServicePointGeolocationCreateModel.builder()
            .spatialReference(SpatialReference.LV95)
            .east(4.64654)
            .build())
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldAllowMinimalServicePointVersionWithLv95Coordinates() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
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

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithLv95CoordinatesMoreThanFiveFractions() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
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

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldAllowMinimalServicePointVersionWithWgs84Coordinates() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
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

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAllowMinimalServicePointVersionWithWgs84CoordinatesMoreThanElevenFractions() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
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

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotAllowServiceWithOperatingPointTypeAndTechnical() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.OPERATING_POINT_BUS)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAllowServiceWithOperatingPointTypeAndTariffPoint() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAllowServiceWithPlainOperatingPointAndStopPoint() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .meansOfTransport(List.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAllowServiceWithPlainOperatingPointAndFreightServicePoint() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .freightServicePoint(true)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldAllowServicePointVersionWithNumberGeneration() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<UpdateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldPassAssertionIsOperatingPointRouteNetworkTrueAndKilometerMasterNumberNullWhenRouteNetworkFalse() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, false);
    assertThat(createServicePointVersionModel.isOperatingPointRouteNetworkTrueAndKilometerMasterNumberNull()).isTrue();
  }

  @Test
  void shouldNotPassAssertionIsOperatingPointRouteNetworkTrueAndKilometerMasterNumberNullWhenRouteNetworkTrueAndKilometerMasterNotNull() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(8034510, true);
    assertThat(createServicePointVersionModel.isOperatingPointRouteNetworkTrueAndKilometerMasterNumberNull()).isFalse();
  }

  @Test
  void shouldPassAssertionIsOperatingPointRouteNetworkTrueAndKilometerMasterNumberNullWhenRouteNetworkTrueAndKilometerMasterNull() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, true);
    assertThat(createServicePointVersionModel.isOperatingPointRouteNetworkTrueAndKilometerMasterNumberNull()).isTrue();
  }

  @Test
  void shouldPassAssertionIsOperatingPointRouteNetworkTrueAndKilometerMasterNumberNullWhenRouteNetworkFalseAndKilometerMasterNull() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, false);
    assertThat(createServicePointVersionModel.isOperatingPointRouteNetworkTrueAndKilometerMasterNumberNull()).isTrue();
  }

  @Test
  void shouldPassAssertionIsOperatingPointRouteNetworkTrueAndKilometerMasterNumberNullWhenRouteNetworkFalseAndKilometerMasterNotNull() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(8034510, false);
    assertThat(createServicePointVersionModel.isOperatingPointRouteNetworkTrueAndKilometerMasterNumberNull()).isTrue();
  }

  @Test
  void shouldPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowed() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, true);
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isFalse();
  }

  @Test
  void shouldPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowedWhenIsFreightServicePoint() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, true);
    createServicePointVersionModel.setFreightServicePoint(true);
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isTrue();
  }

  @Test
  void shouldPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowedWhenOperatingPoint() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, true);
    createServicePointVersionModel.setOperatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.BRANCH);
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isTrue();
  }

  @Test
  void shouldPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowedWhenStopPointAndRouteNetworkTrue() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, true);
    createServicePointVersionModel.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isTrue();
  }

  @Test
  void shouldPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowedWhenStopPoint() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, false);
    createServicePointVersionModel.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isTrue();
  }

  @Test
  void shouldNotPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowedWhenTariffPoint() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, true);
    createServicePointVersionModel.setOperatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT);
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isFalse();
  }

  @Test
  void shouldPassAssertionIsRouteNetworkOrKilometerMasterNumberAllowedWhenTariffPoint() {
    CreateServicePointVersionModel createServicePointVersionModel =
            getCreateServicePointVersionModel(null, false);
    createServicePointVersionModel.setOperatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT);
    assertThat(createServicePointVersionModel.isRouteNetworkOrKilometerMasterNumberAllowed()).isTrue();
  }

  private CreateServicePointVersionModel getCreateServicePointVersionModel(Integer operatingPointKilometerMasterNumber, boolean isOperationPointRouteNetworkTrue) {
    return CreateServicePointVersionModel.builder()
        .numberShort(34510)
        .operatingPointRouteNetwork(isOperationPointRouteNetworkTrue)
        .operatingPointKilometerMasterNumber(operatingPointKilometerMasterNumber)
        .operatingPointType(null)
        .operatingPointTechnicalTimetableType(null)
        .meansOfTransport(null)
        .freightServicePoint(false)
        .operatingPointTrafficPointType(null)
        .build();
  }

}
