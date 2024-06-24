package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StopPointWorkflowExaminantNotFoundExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    StopPointWorkflowExaminantNotFoundException exception = new StopPointWorkflowExaminantNotFoundException();
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.EXAMINANT_MAIL_NOT_FOUND");
  }
}