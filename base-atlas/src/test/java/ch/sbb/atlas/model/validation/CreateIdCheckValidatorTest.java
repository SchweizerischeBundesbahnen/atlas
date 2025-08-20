package ch.sbb.atlas.model.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.model.IdCheckable;
import ch.sbb.atlas.validation.CreateIdCheck;
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
    assertTrue(validator.isValid(obj, null));
  }

  @Test
  void objectWithIdIsInvalid() {
    IdCheckable obj = new SimpleIdCheckable(42L);
    assertFalse(validator.isValid(obj, null));
  }

  class SimpleIdCheckable implements IdCheckable {

    private final Long id;

    public SimpleIdCheckable(Long id) {
      this.id = id;
    }

    @Override
    public Long getId() {
      return id;
    }
  }
}
