package ch.sbb.workflow.exception;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopPointWorkflowAlreadyInAddedStatusException extends BaseWorkflowAlreadyInStatusException {

  @Override
  protected String getExpectedWorkflowStatus() {
    return WorkflowStatus.ADDED.name();
  }

}
