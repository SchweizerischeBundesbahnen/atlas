package ch.sbb.atlas.servicepointdirectory.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class ServicePointNumberTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldGetCountrySuccessfully() {
    assertThat(ServicePointNumber.of(85070003).getCountry()).isEqualTo(Country.SWITZERLAND);
  }

  @Test
  void shouldGetServicePointIdSuccessfully() {
    assertThat(ServicePointNumber.of(85070003).getNumberShort()).isEqualTo(7000);
  }

  @Test
  void shouldCheckDigitSuccessfully() {
    assertThat(ServicePointNumber.of(85070003).getCheckDigit()).isEqualTo(3);
  }

  @Test
  void shouldCheckServicePointNumberLength() {
    Set<ConstraintViolation<ServicePointNumber>> constraintViolations = validator.validate(ServicePointNumber.of(1));
    assertThat(constraintViolations).isNotEmpty();

    constraintViolations = validator.validate(ServicePointNumber.of(85070003));
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldCheckServicePointCountry() {
    Set<ConstraintViolation<ServicePointNumber>> constraintViolations = validator.validate(ServicePointNumber.of(15000001));
    assertThat(constraintViolations).isNotEmpty();
  }

  @Test
  void shouldCalculateCheckDigitCorrectly() {
    assertThat(ServicePointNumber.of(Country.SWITZERLAND, 7000).getCheckDigit()).isEqualTo(3);
    assertThat(ServicePointNumber.of(Country.SWITZERLAND, 92223).getCheckDigit()).isEqualTo(7);
    assertThat(ServicePointNumber.of(Country.SWITZERLAND, 89573).getCheckDigit()).isEqualTo(0);
    assertThat(ServicePointNumber.of(Country.SWITZERLAND, 94267).getCheckDigit()).isEqualTo(2);
    assertThat(ServicePointNumber.of(Country.SWITZERLAND, 90765).getCheckDigit()).isEqualTo(9);
    assertThat(ServicePointNumber.of(Country.SWITZERLAND, 91085).getCheckDigit()).isEqualTo(1);
  }

  @Test
  void shouldBuildServicePointNumberFromSevenDigitNumberSuccessfully() {
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8507000);
    assertThat(servicePointNumber).isEqualTo(ServicePointNumber.of(85070003));
  }
}