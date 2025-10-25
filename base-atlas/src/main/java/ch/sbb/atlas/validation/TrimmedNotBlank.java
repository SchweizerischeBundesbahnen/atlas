package ch.sbb.atlas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {TrimmedNotBlank.Validator.class})
public @interface TrimmedNotBlank {

  String ATLAS_CONSTRAINT_TRIMMED_NOT_BLANK = "{atlas.constraint.trimmedNotBlank}";

  String message() default ATLAS_CONSTRAINT_TRIMMED_NOT_BLANK;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<TrimmedNotBlank, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (Objects.isNull(value)) {
        return true;
      }
      return !value.isEmpty() && value.strip().equals(value);
    }
  }
}
