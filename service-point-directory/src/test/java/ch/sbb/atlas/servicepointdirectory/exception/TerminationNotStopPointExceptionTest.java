package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class TerminationNotStopPointExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    TerminationNotStopPointException exception = new TerminationNotStopPointException();
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "A termination workflow is not allowed when the service point version is not a stop point");
    assertThat(errorResponse.getError()).isEqualTo(
        "Termination workflow not allowed");
  }

}