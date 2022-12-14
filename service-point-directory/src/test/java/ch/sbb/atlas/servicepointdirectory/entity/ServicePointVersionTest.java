package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPlaceType;
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
    void shouldAcceptStopPlaceWithType() {
        // Given
        ServicePointVersion servicePoint = ServicePointVersion.builder()
                .number(1)
                .checkDigit(1)
                .numberShort(1)
                .country(Country.SWITZERLAND)
                .designationLong("long designation")
                .designationOfficial("official designation")
                .abbreviation("BE")
                .statusDidok3(1)
                .businessOrganisation("somesboid")
                .hasGeolocation(true)
                .status(Status.VALIDATED)
                .meansOfTransport(Set.of(MeanOfTransport.COACH))
                .stopPlaceType(StopPlaceType.ORDERLY)
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
    void shouldNotAcceptStopPlaceWithoutMeansOfTransport() {
        // Given
        ServicePointVersion servicePoint = ServicePointVersion.builder()
                .number(1)
                .checkDigit(1)
                .numberShort(1)
                .country(Country.SWITZERLAND)
                .designationLong("long designation")
                .designationOfficial("official designation")
                .abbreviation("BE")
                .statusDidok3(1)
                .businessOrganisation("somesboid")
                .hasGeolocation(true)
                .status(Status.VALIDATED)
                .stopPlaceType(StopPlaceType.ORDERLY)
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2020, 12, 31))
                .version(1)
                .build();
        //when
        Set<ConstraintViolation<ServicePointVersion>> constraintViolations = validator.validate(servicePoint);

        //then
        assertThat(constraintViolations).hasSize(1);
    }

}