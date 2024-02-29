package ch.sbb.prm.directory.validation.status;

import ch.sbb.atlas.model.Status;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PrmStatusSubSetValidator implements ConstraintValidator<PrmStatusSubSet, Status> {

  private Status[] subset;

  @Override
  public void initialize(PrmStatusSubSet constraint) {
    this.subset = constraint.anyOf();
  }

  @Override
  public boolean isValid(Status value, ConstraintValidatorContext context) {
    return Arrays.asList(subset).contains(value) ;
  }
}
