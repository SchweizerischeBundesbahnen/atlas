package ch.sbb.workflow.exception;

import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TerminationStopPointWorkflowAlreadyInStatusException extends BaseWorkflowAlreadyInStatusException {

  private final TerminationWorkflowStatus givenWorkflowStatus;

  @Override
  protected String getExpectedWorkflowStatus() {
    return givenWorkflowStatus.name();
  }
}
