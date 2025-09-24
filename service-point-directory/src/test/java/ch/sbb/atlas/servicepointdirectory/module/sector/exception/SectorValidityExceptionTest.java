package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class SectorValidityExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    SectorValidityException exception = new SectorValidityException();
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("Business rule validation failed");
    assertThat(errorResponse.getError()).isEqualTo("Validity is not in range of validity of traffic point");
  }
}
