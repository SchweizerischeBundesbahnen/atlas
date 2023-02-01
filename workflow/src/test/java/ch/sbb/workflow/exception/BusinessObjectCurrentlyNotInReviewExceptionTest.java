package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BusinessObjectCurrentlyNotInReviewExceptionTest {

  private final BusinessObjectCurrentlyNotInReviewException exception = new BusinessObjectCurrentlyNotInReviewException();

  @Test
  void shouldDisplayConflictCorrectly() {
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(409);
    assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo().getCode()).isEqualTo(
        "WORKFLOW.ERROR.NOT_IN_REVIEW");
  }
}