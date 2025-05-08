package ch.sbb.workflow.sepodi.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.DecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.DecisionModel.DecisionModelBuilder;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DecisionModelTest extends BaseValidatorTest {

  @Test
  void shouldValidateWithJudgementYes() {
    DecisionModel decision = getDecisionBuilder()
        .judgement(JudgementType.YES)
        .build();
    Set<ConstraintViolation<DecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldValidateWithJudgementNoAndMotivation() {
    DecisionModel decision = getDecisionBuilder()
        .judgement(JudgementType.NO)
        .motivation("No, please")
        .build();
    Set<ConstraintViolation<DecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldFailToValidateWithJudgementNoAndNoMotivation() {
    DecisionModel decision = getDecisionBuilder()
        .judgement(JudgementType.NO)
        .build();
    Set<ConstraintViolation<DecisionModel>> constraintViolations = validator.validate(decision);
    assertThat(constraintViolations).hasSize(1);
  }

  private static DecisionModelBuilder<?, ?> getDecisionBuilder() {
    return DecisionModel.builder()
        .firstName("Daniel")
        .lastName("Berlin")
        .organisation("Regierung")
        .personFunction("Cheffe")
        .examinantMail("daniel@berlin.com")
        .pinCode("1246");
  }

}