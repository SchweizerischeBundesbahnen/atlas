package ch.sbb.atlas.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseValidatorTest {

  protected ValidatorFactory validatorFactory;
  protected Validator validator;

  @BeforeEach
  void createValidator() {
    Locale.setDefault(Locale.ENGLISH);
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterEach
  void close() {
    validatorFactory.close();
  }

}
