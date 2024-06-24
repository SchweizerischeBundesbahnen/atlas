package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StopPointWorkflowNotInHearingExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    StopPointWorkflowNotInHearingException exception = new StopPointWorkflowNotInHearingException();
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.NOT_IN_HEARING");
  }
}