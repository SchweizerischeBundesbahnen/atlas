package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StopPointWorkflowStatusMustBeHearingExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    StopPointWorkflowStatusMustBeHearingException exception = new StopPointWorkflowStatusMustBeHearingException();
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_HEARING");
  }
}