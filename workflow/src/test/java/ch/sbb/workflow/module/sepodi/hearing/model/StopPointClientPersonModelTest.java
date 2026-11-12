package ch.sbb.workflow.module.sepodi.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointClientPersonModel.StopPointClientPersonModelBuilder;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StopPointClientPersonModelTest extends BaseValidatorTest {

  @Test
  void shouldValidateWithNullFirstNameAndLastName() {
    StopPointClientPersonModel examinant = getExaminantBuilder().build();
    Set<ConstraintViolation<StopPointClientPersonModel>> constraintViolations = validator.validate(examinant);
    assertThat(constraintViolations).isEmpty();
  }

  // Organisation-only examinants (e.g. cantonal offices configured under "examinants.cantons") have no
  // personal contact and are intentionally stored with blank firstName/lastName (ATLAS-3621).
  @Test
  void shouldValidateWithBlankFirstNameAndLastName() {
    StopPointClientPersonModel examinant = getExaminantBuilder().firstName("").lastName("").build();
    Set<ConstraintViolation<StopPointClientPersonModel>> constraintViolations = validator.validate(examinant);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldFailToValidateWithBlankOrganisation() {
    StopPointClientPersonModel examinant = getExaminantBuilder().organisation("   ").build();
    Set<ConstraintViolation<StopPointClientPersonModel>> constraintViolations = validator.validate(examinant);
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("organisation");
  }

  private static StopPointClientPersonModelBuilder<?, ?> getExaminantBuilder() {
    return StopPointClientPersonModel.builder()
        .organisation("Verkehrsplanung")
        .mail("examinant@sbb.ch");
  }

}
