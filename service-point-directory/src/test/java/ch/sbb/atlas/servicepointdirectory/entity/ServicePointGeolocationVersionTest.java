package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ServicePointGeolocationVersionTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAllowFourDigitsOnGeolocationHeight() {
    // given
    ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600037.945)
        .north(1199749.812)
        .height(2540.2112)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .version(0)
        .build();

    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .servicePointGeolocation(servicePointGeolocation)
        .version(0)
        .build();

    servicePointGeolocation.setServicePointVersion(servicePoint);

    //when
    Set<ConstraintViolation<ServicePointGeolocation>> constraintViolations = validator.validate(servicePointGeolocation);

    //then
    assertThat(constraintViolations).isEmpty();
  }
}
