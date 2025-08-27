package ch.sbb.workflow.module.sepodi.termination.exception;

import ch.sbb.workflow.exception.BaseWorkflowAlreadyInStatusException;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TerminationStopPointWorkflowAlreadyInStatusException extends BaseWorkflowAlreadyInStatusException {

  private final TerminationWorkflowStatus givenWorkflowStatus;

  @Override
  protected String getExpectedWorkflowStatus() {
    return givenWorkflowStatus.name();
  }
}
