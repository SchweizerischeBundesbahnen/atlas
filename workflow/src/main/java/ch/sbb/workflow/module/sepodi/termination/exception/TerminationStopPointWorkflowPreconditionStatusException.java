package ch.sbb.workflow.module.sepodi.termination.exception;

import ch.sbb.workflow.exception.BaseWorkflowPreconditionStatusException;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TerminationStopPointWorkflowPreconditionStatusException extends BaseWorkflowPreconditionStatusException {

  private final TerminationWorkflowStatus expectedWorkflowStatus;

  @Override
  protected String getExpectedWorkflowStatus() {
    return expectedWorkflowStatus.name();
  }

}