package ch.sbb.workflow.sepodi.hearing.service;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.entity.Person;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StopPointWorkflowProgressDecider {

  private final Map<Person, Optional<Decision>> decisions;

  public boolean areAllDecisionsMade() {
    return decisions.values().stream().allMatch(Optional::isPresent);
  }

  public Optional<WorkflowStatus> calculateNewWorkflowStatus() {
    if (areAllDecisionsMade()) {
      if (decisions.values().stream().allMatch(i -> i.orElseThrow().getWeightedJudgement() == JudgementType.YES)) {
        return Optional.of(WorkflowStatus.APPROVED);
      }
    }
    if (decisions.values().stream()
        .filter(Optional::isPresent).map(Optional::get)
        .anyMatch(i -> i.getFotJudgement() == JudgementType.NO)) {
      return Optional.of(WorkflowStatus.REJECTED);
    }
    return Optional.empty();
  }

  public String getRejectComment() {
    return decisions.values().stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(i -> i.getFotJudgement() == JudgementType.NO)
        .map(Decision::getFotMotivation)
        .findFirst().orElseThrow();
  }
}
