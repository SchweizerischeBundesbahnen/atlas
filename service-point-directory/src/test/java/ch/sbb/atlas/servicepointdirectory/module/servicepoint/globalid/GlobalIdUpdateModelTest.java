package ch.sbb.atlas.servicepointdirectory.module.servicepoint.globalid;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasFieldLengths;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GlobalIdUpdateModelTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private Set<ConstraintViolation<GlobalIdUpdateModel>> validate(String globalId) {
    return validator.validate(GlobalIdUpdateModel.builder().globalId(globalId).build());
  }

  @Test
  void shouldAcceptValidGlobalId() {
    // When / Then
    assertThat(validate("de:05770:1282")).isEmpty();
  }

  @Test
  void shouldRejectNullGlobalId() {
    // When / Then
    assertThat(validate(null)).isNotEmpty();
  }

  @Test
  void shouldRejectBlankGlobalId() {
    // When / Then
    assertThat(validate("   ")).isNotEmpty();
  }

  @Test
  void shouldRejectGlobalIdWithLeadingWhitespace() {
    // When / Then
    assertThat(validate(" de:05770:1282")).isNotEmpty();
  }

  @Test
  void shouldRejectGlobalIdWithTrailingWhitespace() {
    // When / Then
    assertThat(validate("de:05770:1282 ")).isNotEmpty();
  }

  @Test
  void shouldRejectGlobalIdExceedingMaxLength() {
    // Given
    String tooLong = "de:" + "1".repeat(AtlasFieldLengths.LENGTH_128);

    // When / Then
    assertThat(validate(tooLong)).isNotEmpty();
  }

}
