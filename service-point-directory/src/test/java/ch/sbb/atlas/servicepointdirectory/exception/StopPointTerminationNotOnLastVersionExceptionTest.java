package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class StopPointTerminationNotOnLastVersionExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    StopPointTerminationNotOnLastVersionException exception = new StopPointTerminationNotOnLastVersionException();
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(417);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "The StopPoint version is not the oldest version: termination is only possible on the oldest of StopPoint version");
  }

}