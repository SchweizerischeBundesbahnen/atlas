package ch.sbb.atlas.workflow.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum WorkflowProcessingStatus {

  EVALUATED,
  IN_PROGRESS;

  public static WorkflowProcessingStatus getProcessingStatus(WorkflowStatus workflowStatus) {
    if (WorkflowStatus.APPROVED == workflowStatus || WorkflowStatus.REJECTED == workflowStatus) {
      return EVALUATED;
    }
    return IN_PROGRESS;
  }
}
