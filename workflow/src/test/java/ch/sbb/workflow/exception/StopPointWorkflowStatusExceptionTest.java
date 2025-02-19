package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import org.junit.jupiter.api.Test;

class StopPointWorkflowStatusExceptionTest {

  @Test
  void shouldHaveCorrectErrorCodeForHearing() {
    StopPointWorkflowStatusException exception = new StopPointWorkflowStatusException(WorkflowStatus.HEARING);
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_HEARING");
  }

  @Test
  void shouldHaveCorrectErrorCodeForAdded() {
    StopPointWorkflowStatusException exception = new StopPointWorkflowStatusException(WorkflowStatus.ADDED);
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_ADDED");
  }
}