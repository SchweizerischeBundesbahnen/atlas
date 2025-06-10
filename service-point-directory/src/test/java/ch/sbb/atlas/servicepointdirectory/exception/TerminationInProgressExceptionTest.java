package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TerminationInProgressExceptionTest {

  @Test
  void shouldGetErrorMessage() {
    TerminationInProgressException exception = new TerminationInProgressException();

    assertThat(exception.getErrorResponse().getMessage()).isEqualTo(
        "StopPoint cannot be edited because a termination is in progress");
  }

}