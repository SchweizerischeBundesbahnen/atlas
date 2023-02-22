package ch.sbb.atlas.servicepointdirectory.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointWithoutTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
        .statusDidok3(ServicePointStatus.from(1))
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
        .statusDidok3(ServicePointStatus.from(1))
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
        .statusDidok3(ServicePointStatus.from(1))
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
        .statusDidok3(ServicePointStatus.from(1))
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
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .operatingPointWithoutTimetableType(OperatingPointWithoutTimetableType.ROUTE_SPEED_CHANGE)
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
