package ch.sbb.atlas.validation;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ValidStopPointType.Validator.class})
public @interface ValidStopPointType {

  String ATLAS_CONSTRAINT_VALID_STOP_POINT_TYPE = "{atlas.constraint.validStopPointType}";

  String message() default ATLAS_CONSTRAINT_VALID_STOP_POINT_TYPE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<ValidStopPointType, UpdateServicePointVersionModel> {

    @Override
    public boolean isValid(UpdateServicePointVersionModel model, ConstraintValidatorContext context) {
      return (!model.getMeansOfTransport().isEmpty() || model.getStopPointType() == null) && (
          model.getMeansOfTransport().isEmpty() || (model.getStopPointType() != null
              && StopPointType.validOnInput()
              .contains(model.getStopPointType())));
    }
  }
}
