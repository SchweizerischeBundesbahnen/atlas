package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TerminationStopPointWorkflowConflictExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    TerminationStopPointWorkflowConflictException exception = new TerminationStopPointWorkflowConflictException();
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("A termination workflow is in progress!");
  }

}