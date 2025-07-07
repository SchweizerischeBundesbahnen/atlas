package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class TrafficPointNotFoundExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    TrafficPointNotFoundException exception = new TrafficPointNotFoundException("ch:sloid:1");
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(404);
    assertThat(errorResponse.getMessage()).isEqualTo("Traffic Point Element not found with sloid: ch:sloid:1");
  }
}
