package ch.sbb.atlas.api.location;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.Country;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GenerateSloidRequestModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldHaveErrorWhenNoCountrySet() {
    // given
    GenerateSloidRequestModel model = new GenerateSloidRequestModel(SloidType.SERVICE_POINT, "ch:1:sloid:1");

    // when
    Set<ConstraintViolation<GenerateSloidRequestModel>> violations = validator.validate(model);

    // then
    assertThat(violations).hasSize(1);
  }

  @Test
  void shouldHaveErrorWhenNotValidCountrySet() {
    // given
    GenerateSloidRequestModel model = new GenerateSloidRequestModel(SloidType.SERVICE_POINT, Country.ALBANIA);

    // when
    Set<ConstraintViolation<GenerateSloidRequestModel>> violations = validator.validate(model);

    // then
    assertThat(violations).hasSize(1);
  }

  @Test
  void shouldNotHaveErrorWhenValidCountrySet() {
    // given
    GenerateSloidRequestModel model = new GenerateSloidRequestModel(SloidType.SERVICE_POINT, Country.ITALY_BUS);

    // when
    Set<ConstraintViolation<GenerateSloidRequestModel>> violations = validator.validate(model);

    // then
    assertThat(violations).isEmpty();
  }

  @Test
  void shouldHaveErrorWhenSloidPrefixNotSet() {
    // given
    GenerateSloidRequestModel model = new GenerateSloidRequestModel(SloidType.TOILET, Country.ALGERIA);

    // when
    Set<ConstraintViolation<GenerateSloidRequestModel>> violations = validator.validate(model);

    // then
    assertThat(violations).hasSize(1);
  }

  @Test
  void shouldNotHaveErrorWhenSloidPrefixSet() {
    // given
    GenerateSloidRequestModel model = new GenerateSloidRequestModel(SloidType.TOILET, "ch:1:sloid:1");

    // when
    Set<ConstraintViolation<GenerateSloidRequestModel>> violations = validator.validate(model);

    // then
    assertThat(violations).isEmpty();
  }

}
