package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.JudgementType;
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
    if (decisions.values().stream().filter(Optional::isPresent).anyMatch(i -> i.orElseThrow().getFotJudgement() == JudgementType.NO)) {
      return Optional.of(WorkflowStatus.REJECTED);
    }
    return Optional.empty();
  }

}
