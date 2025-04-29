package ch.sbb.workflow.exception;

import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TerminationStopPointWorkflowPreconditionStatusException extends BaseWorkflowPreconditionStatusException {

  private final TerminationWorkflowStatus expectedWorkflowStatus;

  @Override
  protected String getExpectedWorkflowStatus() {
    return expectedWorkflowStatus.name();
  }

}