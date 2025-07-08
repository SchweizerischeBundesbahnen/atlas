package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class SectorNotExistingExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    SectorNotExistingException exception = new SectorNotExistingException("ch:sloid:1");
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(404);
    assertThat(errorResponse.getMessage()).isEqualTo("No sector exists with sloid ch:sloid:1");
  }
}
