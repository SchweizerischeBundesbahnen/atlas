package ch.sbb.workflow.module.sepodi.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.workflow.module.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OverrideDecisionModel.OverrideDecisionModelBuilder;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OverrideDecisionModelTest extends BaseValidatorTest {

  @Test
  void shouldValidateWithJudgementYes() {
    OverrideDecisionModel decision = getOverrideDecisionBuilder()
        .fotJudgement(JudgementType.YES)
        .build();
    Set<ConstraintViolation<OverrideDecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldValidateWithJudgementNoAndMotivation() {
    OverrideDecisionModel decision = getOverrideDecisionBuilder()
        .fotJudgement(JudgementType.NO)
        .fotMotivation("No, please")
        .build();
    Set<ConstraintViolation<OverrideDecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldFailToValidateWithJudgementNoAndNoMotivation() {
    OverrideDecisionModel decision = getOverrideDecisionBuilder()
        .fotJudgement(JudgementType.NO)
        .build();
    Set<ConstraintViolation<OverrideDecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldFailToValidateWithBlankFirstName() {
    OverrideDecisionModel decision = getOverrideDecisionBuilder()
        .fotJudgement(JudgementType.YES)
        .firstName("   ")
        .build();
    Set<ConstraintViolation<OverrideDecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("firstName");
  }

  @Test
  void shouldFailToValidateWithBlankLastName() {
    OverrideDecisionModel decision = getOverrideDecisionBuilder()
        .fotJudgement(JudgementType.YES)
        .lastName("   ")
        .build();
    Set<ConstraintViolation<OverrideDecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("lastName");
  }

  private static OverrideDecisionModelBuilder<?, ?> getOverrideDecisionBuilder() {
    return OverrideDecisionModel.builder()
        .firstName("Uwe")
        .lastName("Berlin");
  }
}