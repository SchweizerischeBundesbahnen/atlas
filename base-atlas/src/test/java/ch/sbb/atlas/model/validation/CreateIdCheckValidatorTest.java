package ch.sbb.atlas.model.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.model.IdCheckable;
import ch.sbb.atlas.validation.CreateIdCheck;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateIdCheckValidatorTest {

  private CreateIdCheck.Validator validator;

  @BeforeEach
  void setUp() {
    validator = new CreateIdCheck.Validator();
  }

  @Test
  void objectWithoutIdIsValid() {
    IdCheckable obj = new SimpleIdCheckable(null);
    assertTrue(validator.isValid(obj, dummyContext()));
  }

  @Test
  void objectWithIdIsInvalid() {
    IdCheckable obj = new SimpleIdCheckable(42L);
    assertFalse(validator.isValid(obj, dummyContext()));
  }

  private ConstraintValidatorContext dummyContext() {
    return null;
  }
}
