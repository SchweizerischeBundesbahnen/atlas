package ch.sbb.atlas.validation;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {AllowedMeanOfTransport.Validator.class})
public @interface AllowedMeanOfTransport {

  String message() default "{atlas.constraint.allowedMeanOfTransport}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  MeanOfTransport[] allowed();

  class Validator implements ConstraintValidator<AllowedMeanOfTransport, MeanOfTransport> {

    private EnumSet<MeanOfTransport> allowed;

    @Override
    public void initialize(AllowedMeanOfTransport constraintAnnotation) {
      this.allowed = constraintAnnotation.allowed().length == 0
          ? EnumSet.noneOf(MeanOfTransport.class)
          : EnumSet.copyOf(Arrays.asList(constraintAnnotation.allowed()));
    }

    @Override
    public boolean isValid(MeanOfTransport value, ConstraintValidatorContext context) {
      if (Objects.isNull(value)) {
        return true;
      }
      return allowed.contains(value);
    }
  }
}
