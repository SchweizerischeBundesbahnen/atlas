package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StopPointWorkflowPinCodeInvalidExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    StopPointWorkflowPinCodeInvalidException exception = new StopPointWorkflowPinCodeInvalidException();
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.PIN_CODE_INVALID");
  }

}