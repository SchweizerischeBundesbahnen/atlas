package ch.sbb.workflow.exception;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopPointWorkflowPreconditionStatusException extends BaseWorkflowPreconditionStatusException {

  private final WorkflowStatus expectedWorkflowStatus;

  @Override
  protected String getExpectedWorkflowStatus() {
    return expectedWorkflowStatus.name();
  }

}
