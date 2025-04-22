package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.hearing.service.StopPointWorkflowProgressDecider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StopPointWorkflowProgressDeciderTest {

  private static final Person JULY = Person.builder().mail("july@summer.ch").build();
  private static final Person AUGUST = Person.builder().mail("august@summer.ch").build();

  @Test
  void shouldDontDoAnythingOnMissingVotes() {
    Map<Person, Optional<Decision>> decisions = Map.of(JULY, Optional.empty());

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isFalse();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEmpty();
  }

  @Test
  void shouldApproveWorkflowIfAllExaminantsApprove() {
    Map<Person, Optional<Decision>> decisions = Map.of(JULY,
        Optional.of(Decision.builder().judgement(JudgementType.YES).build()));

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isTrue();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEqualTo(Optional.of(WorkflowStatus.APPROVED));
  }

  @Test
  void shouldDoNothingIfOneExaminantSaysNo() {
    Map<Person, Optional<Decision>> decisions = new HashMap<>();
    decisions.put(JULY, Optional.of(Decision.builder().judgement(JudgementType.YES).build()));
    decisions.put(AUGUST, Optional.of(Decision.builder().judgement(JudgementType.NO).build()));

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isTrue();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEmpty();
  }

  @Test
  void shouldApproveWorkflowIfOneExaminantSaysNoButBavOverrides() {
    Map<Person, Optional<Decision>> decisions = new HashMap<>();
    decisions.put(JULY, Optional.of(Decision.builder().judgement(JudgementType.YES).build()));
    decisions.put(AUGUST, Optional.of(Decision.builder().judgement(JudgementType.NO).fotJudgement(JudgementType.YES).build()));

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isTrue();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEqualTo(Optional.of(WorkflowStatus.APPROVED));
  }

  @Test
  void shouldDoNothingOnOneNo() {
    Map<Person, Optional<Decision>> decisions = new HashMap<>();
    decisions.put(AUGUST, Optional.of(Decision.builder().judgement(JudgementType.NO).build()));

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isTrue();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEmpty();
  }

  @Test
  void shouldRejectWorkflowIfBavAlsoSaysNo() {
    Map<Person, Optional<Decision>> decisions = new HashMap<>();
    decisions.put(AUGUST, Optional.of(Decision.builder().judgement(JudgementType.NO).fotJudgement(JudgementType.NO).build()));

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isTrue();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEqualTo(Optional.of(WorkflowStatus.REJECTED));
  }

  @Test
  void shouldRejectWorkflowIfBavSaysNoWithoutWaitingForAll() {
    Map<Person, Optional<Decision>> decisions = new HashMap<>();
    decisions.put(JULY, Optional.empty());
    decisions.put(AUGUST, Optional.of(Decision.builder().judgement(JudgementType.NO).fotJudgement(JudgementType.NO).build()));

    StopPointWorkflowProgressDecider stopPointWorkflowProgressDecider = new StopPointWorkflowProgressDecider(decisions);

    assertThat(stopPointWorkflowProgressDecider.areAllDecisionsMade()).isFalse();
    assertThat(stopPointWorkflowProgressDecider.calculateNewWorkflowStatus()).isEqualTo(Optional.of(WorkflowStatus.REJECTED));
  }

}