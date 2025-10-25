package ch.sbb.atlas.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TrimmedNotBlankValidatorTest {

  private final TrimmedNotBlank.Validator validator = new TrimmedNotBlank.Validator();

  @Test
  void shouldTreatNullStringAsValid() {
    assertThat(validator.isValid(null, null)).isTrue();
  }

  @Test
  void shouldTreatEmptyStringAsInvalid() {
    assertThat(validator.isValid("", null)).isFalse();
  }

  @Test
  void shouldTreatOnlyWhitespaceAsInvalid() {
    assertThat(validator.isValid(" ", null)).isFalse();
    assertThat(validator.isValid("\t", null)).isFalse();
    assertThat(validator.isValid("\n", null)).isFalse();
    assertThat(validator.isValid("   ", null)).isFalse();
  }

  @Test
  void shouldTreatTrimmedNonEmptyAsValid() {
    assertThat(validator.isValid("a", null)).isTrue();
    assertThat(validator.isValid("abc", null)).isTrue();
    assertThat(validator.isValid("a b", null)).isTrue();
  }

  @Test
  void shouldTreatLeadingOrTrailingWhitespaceAsInvalid() {
    assertThat(validator.isValid(" abc", null)).isFalse();
    assertThat(validator.isValid("abc ", null)).isFalse();
    assertThat(validator.isValid(" abc ", null)).isFalse();
    assertThat(validator.isValid("\tabc", null)).isFalse();
    assertThat(validator.isValid("abc\n", null)).isFalse();
  }
}
