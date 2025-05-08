package ch.sbb.workflow.sepodi.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OverrideDecisionModel.OverrideDecisionModelBuilder;
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

  private static OverrideDecisionModelBuilder<?, ?> getOverrideDecisionBuilder() {
    return OverrideDecisionModel.builder()
        .firstName("Daniel")
        .lastName("Berlin");
  }

}