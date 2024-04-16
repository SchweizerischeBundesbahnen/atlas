package ch.sbb.atlas.api.timetable.hearing;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmailsValidator.class)
public @interface ValidEmails {
  String message() default "Invalid email address";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
