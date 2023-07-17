package ch.sbb.atlas.servicepointdirectory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePointVersionTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAcceptStopPointWithType() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.of(85070003))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<ServicePointVersion>> constraintViolations = validator.validate(servicePoint);

    //then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAcceptStopPointWithoutMeansOfTransport() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.of(85070003))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<ServicePointVersion>> constraintViolations = validator.validate(servicePoint);

    //then
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldAcceptFreightServicePointWithSortCodeOfDestinationStation() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.of(10070003))
        .numberShort(7000)
        .country(Country.FINLAND)
        .freightServicePoint(true)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<ServicePointVersion>> constraintViolations = validator.validate(servicePoint);

    //then
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldNotAcceptFreightServicePointWithoutSortCodeOfDestinationStationInSwitzerland() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.of(85070003))
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .freightServicePoint(true)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now())
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<ServicePointVersion>> constraintViolations = validator.validate(servicePoint);

    //then
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldNotAcceptSpeedChangeAndTariffPoint() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.of(85070003))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.ROUTE_SPEED_CHANGE)
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .version(1)
        .build();
    //when
    Set<ConstraintViolation<ServicePointVersion>> constraintViolations = validator.validate(servicePoint);

    //then
    assertThat(constraintViolations).isNotEmpty();
  }
}
