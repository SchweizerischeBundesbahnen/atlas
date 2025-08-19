package ch.sbb.atlas.validation;

import ch.sbb.atlas.model.IdCheckable;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {CreateIdCheck.Validator.class})
public @interface CreateIdCheck {

  String message() default "{atlas.constraint.createIdCheck}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<CreateIdCheck, IdCheckable> {

    @Override
    public boolean isValid(IdCheckable identifiable, ConstraintValidatorContext context) {
      return identifiable.getId() == null;
    }
  }
}
