package ch.sbb.workflow.module.sepodi.hearing.exception;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.exception.BaseWorkflowAlreadyInStatusException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopPointWorkflowAlreadyInAddedStatusException extends BaseWorkflowAlreadyInStatusException {

  @Override
  protected String getExpectedWorkflowStatus() {
    return WorkflowStatus.ADDED.name();
  }

}
