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
import java.util.function.Predicate;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ValidTtfnDescription.Validator.class})
public @interface ValidTtfnDescription {

  String message() default "{atlas.constraint.validTtfnDescription}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<ValidTtfnDescription, TimetableFieldNumberVersionModel> {

    private final Predicate<TimetableFieldNumberVersionModel> outwardLineTwoOnlyFillableIfOutwardLineOneHasAtLeastTwoChars =
        model -> hasAtLeastTwoCharsIfPresent(model.getDescriptionOutwardLine1(), model.getDescriptionOutwardLine2());
    private final Predicate<TimetableFieldNumberVersionModel> outwardLineThreeOnlyFillableIfOutwardLineTwoHasAtLeastTwoChars =
        model -> hasAtLeastTwoCharsIfPresent(model.getDescriptionOutwardLine2(), model.getDescriptionOutwardLine3());
    private final Predicate<TimetableFieldNumberVersionModel> returnLineTwoOnlyFillableIfReturnLineOneHasAtLeastTwoChars =
        model -> hasAtLeastTwoCharsIfPresent(model.getDescriptionReturnLine1(), model.getDescriptionReturnLine2());
    private final Predicate<TimetableFieldNumberVersionModel> returnLineThreeOnlyFillableIfReturnLineTwoHasAtLeastTwoChars =
        model -> hasAtLeastTwoCharsIfPresent(model.getDescriptionReturnLine2(), model.getDescriptionReturnLine3());

    @Override
    public boolean isValid(TimetableFieldNumberVersionModel model, ConstraintValidatorContext context) {
      return outwardLineTwoOnlyFillableIfOutwardLineOneHasAtLeastTwoChars
          .and(outwardLineThreeOnlyFillableIfOutwardLineTwoHasAtLeastTwoChars)
          .and(returnLineTwoOnlyFillableIfReturnLineOneHasAtLeastTwoChars)
          .and(returnLineThreeOnlyFillableIfReturnLineTwoHasAtLeastTwoChars)
          .test(model);
    }

    private boolean hasAtLeastTwoCharsIfPresent(String s1, String s2) {
      return isEmpty(s2) || hasAtLeastTwoChars(s1);
    }

    private boolean isEmpty(String s) {
      return Objects.isNull(s) || s.isEmpty();
    }

    private boolean hasAtLeastTwoChars(String s) {
      return Objects.nonNull(s) && s.length() >= 2;
    }
  }
}
