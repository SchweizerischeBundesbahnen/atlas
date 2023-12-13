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
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ServicePointVersionTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAcceptStopPointWithType() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1007000))
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
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

  @Test
   void servicePointSharedEntityIntegrityTest(){
    //given

    //when
    AtomicInteger result = new AtomicInteger();
    Arrays.stream(ServicePointVersion.class.getClasses()).forEach(c -> result.addAndGet(c.getDeclaredFields().length));

    //then
    String errorDescription = String.format("\n The %s is used in ServicePointDirectory project. " +
            "If this test fail please make sure the entire ATLAS application works properly: import, export, ...\n", ServicePointVersion.class);
    assertThat(result.get()).as(errorDescription).isEqualTo(62);
  }

  @Test
  void shouldAcceptWintherthurAbbreviation() {
    // Given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8506000))
        .numberShort(6000)
        .country(Country.SWITZERLAND)
        .freightServicePoint(true)
        .designationLong("Winterthur")
        .designationOfficial("official designation")
        .abbreviation("W")
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
}
