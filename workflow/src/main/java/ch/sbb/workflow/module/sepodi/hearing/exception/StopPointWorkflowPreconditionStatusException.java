package ch.sbb.workflow.module.sepodi.hearing.exception;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.exception.BaseWorkflowPreconditionStatusException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopPointWorkflowPreconditionStatusException extends BaseWorkflowPreconditionStatusException {

  private final WorkflowStatus expectedWorkflowStatus;

  @Override
  protected String getExpectedWorkflowStatus() {
    return expectedWorkflowStatus.name();
  }

}
