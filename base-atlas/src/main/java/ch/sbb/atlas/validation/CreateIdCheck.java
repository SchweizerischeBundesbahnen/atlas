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

  String ATLAS_CONSTRAINT_CREATE_ID_CHECK = "{atlas.constraint.createIdCheck}";

  String message() default ATLAS_CONSTRAINT_CREATE_ID_CHECK;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<CreateIdCheck, IdCheckable> {

    @Override
    public boolean isValid(IdCheckable idCheckable, ConstraintValidatorContext context) {
      return idCheckable.getId() == null;
    }
  }
}
