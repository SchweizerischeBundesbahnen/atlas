package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
            .operatingPointType(OperatingPointType.STOP_POINT)
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
    void shouldNotAcceptFreightServicePointWithoutSortCodeOfDestinationStation() {
        // Given
        ServicePointVersion servicePoint = ServicePointVersion.builder()
            .number(ServicePointNumber.of(85070003))
            .numberShort(7000)
            .country(Country.SWITZERLAND)
            .designationLong("long designation")
            .designationOfficial("official designation")
            .abbreviation("BE")
            .statusDidok3(ServicePointStatus.from(1))
            .businessOrganisation("somesboid")
            .status(Status.VALIDATED)
            .operatingPointType(OperatingPointType.FREIGHT_POINT)
            .sortCodeOfDestinationStation(null)
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
            .number(ServicePointNumber.of(85070003))
            .numberShort(7000)
            .country(Country.SWITZERLAND)
            .designationLong("long designation")
            .designationOfficial("official designation")
            .abbreviation("BE")
            .statusDidok3(ServicePointStatus.from(1))
            .businessOrganisation("somesboid")
            .status(Status.VALIDATED)
            .operatingPointType(OperatingPointType.FREIGHT_POINT)
            .sortCodeOfDestinationStation("654")
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
