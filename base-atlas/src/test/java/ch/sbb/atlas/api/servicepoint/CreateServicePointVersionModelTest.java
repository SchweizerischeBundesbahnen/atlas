package ch.sbb.atlas.api.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CreateServicePointVersionModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldNotBeValidWhenNumberShortGivenButCountryInAutomaticServicePointId() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldNotBeValidWhenNumberShortNullButCountryNotInAutomaticServicePointId() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .country(Country.JAPAN)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldBeValidWhenNumberShortNullAndCountryInAutomaticServicePointId() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldBeValidWhenNumberShortGivenAndCountryNotInAutomaticServicePointId() {
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.JAPAN)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Set<ConstraintViolation<CreateServicePointVersionModel>> constraintViolations = validator.validate(servicePointVersionModel);
    assertThat(constraintViolations).isEmpty();
  }

}
