package ch.sbb.atlas.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrimmedNotBlankValidatorTest {

  private final TrimmedNotBlank.Validator validator = new TrimmedNotBlank.Validator();

  @Test
  void nullIsValid() {
    assertTrue(validator.isValid(null, null));
  }

  @Test
  void emptyStringIsInvalid() {
    assertFalse(validator.isValid("", null));
  }

  @Test
  void onlyWhitespaceIsInvalid() {
    assertFalse(validator.isValid(" ", null));
    assertFalse(validator.isValid("\t", null));
    assertFalse(validator.isValid("\n", null));
    assertFalse(validator.isValid("   ", null));
  }

  @Test
  void trimmedNonEmptyIsValid() {
    assertTrue(validator.isValid("a", null));
    assertTrue(validator.isValid("abc", null));
    assertTrue(validator.isValid("a b", null));
  }

  @Test
  void leadingOrTrailingWhitespaceIsInvalid() {
    assertFalse(validator.isValid(" abc", null));
    assertFalse(validator.isValid("abc ", null));
    assertFalse(validator.isValid(" abc ", null));
    assertFalse(validator.isValid("\tabc", null));
    assertFalse(validator.isValid("abc\n", null));
  }
}
