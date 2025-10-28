package ch.sbb.atlas.validation;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
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
import org.springframework.util.StringUtils;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ValidTtfnDescription.Validator.class})
public @interface ValidTtfnDescription {

  String ATLAS_CONSTRAINT_VALID_TTFN_DESCRIPTION = "{atlas.constraint.validTtfnDescription}";

  String message() default ATLAS_CONSTRAINT_VALID_TTFN_DESCRIPTION;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<ValidTtfnDescription, TimetableFieldNumberVersionModel> {

    @Override
    public boolean isValid(TimetableFieldNumberVersionModel model, ConstraintValidatorContext context) {
      return firstHasAtLeastTwoCharsIfSecondIsPresent(model.getDescriptionOutwardLine1(), model.getDescriptionOutwardLine2())
          && firstHasAtLeastTwoCharsIfSecondIsPresent(model.getDescriptionOutwardLine2(), model.getDescriptionOutwardLine3())
          && firstHasAtLeastTwoCharsIfSecondIsPresent(model.getDescriptionReturnLine1(), model.getDescriptionReturnLine2())
          && firstHasAtLeastTwoCharsIfSecondIsPresent(model.getDescriptionReturnLine2(), model.getDescriptionReturnLine3());
    }

    private boolean firstHasAtLeastTwoCharsIfSecondIsPresent(String first, String second) {
      return !StringUtils.hasLength(second) || hasAtLeastTwoChars(first);
    }

    private boolean hasAtLeastTwoChars(String s) {
      return Objects.nonNull(s) && s.length() >= 2;
    }
  }
}
