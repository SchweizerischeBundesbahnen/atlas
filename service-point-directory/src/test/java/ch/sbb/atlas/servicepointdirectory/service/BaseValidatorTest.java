package ch.sbb.atlas.servicepointdirectory.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseValidatorTest {
  protected ValidatorFactory validatorFactory;
  protected Validator validator;

  @BeforeEach
   void createValidator() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterEach
   void close() {
    validatorFactory.close();
  }

}
