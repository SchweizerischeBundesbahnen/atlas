package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class MissingTrainStopPointExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    MissingTrainStopPointException exception = new MissingTrainStopPointException();
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Expected at least one service point version that is a stop point and has only TRAIN as means of transport.");
  }
}
