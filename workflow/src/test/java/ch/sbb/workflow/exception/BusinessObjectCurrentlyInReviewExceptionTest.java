package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BusinessObjectCurrentlyInReviewExceptionTest {

  private final BusinessObjectCurrentlyInReviewException exception = new BusinessObjectCurrentlyInReviewException();

  @Test
  void shouldDisplayConflictCorrectly() {
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(409);
    assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.ALREADY_IN_REVIEW");
  }
}