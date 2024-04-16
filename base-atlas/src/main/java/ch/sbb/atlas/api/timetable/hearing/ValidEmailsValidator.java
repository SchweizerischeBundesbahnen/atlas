package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.regex.Pattern;

public class ValidEmailsValidator implements ConstraintValidator<ValidEmails, Set<String>> {

  @Override
  public void initialize(ValidEmails constraintAnnotation) {
    // Initialization logic
  }

  @Override
  public boolean isValid(Set<String> emails, ConstraintValidatorContext context) {
    if (emails == null) {
      return true; // Null values are considered valid
    }

    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.EMAIL_ADDRESS);
    for (String email : emails) {
      if (!pattern.matcher(email).matches()) {
        return false; // Invalid email found
      }
    }

    return true; // All emails are valid
  }
}
