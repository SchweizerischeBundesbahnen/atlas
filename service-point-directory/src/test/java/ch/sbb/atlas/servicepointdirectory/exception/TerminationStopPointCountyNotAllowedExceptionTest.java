package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.servicepoint.Country;
import org.junit.jupiter.api.Test;

class TerminationStopPointCountyNotAllowedExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    TerminationStopPointCountyNotAllowedException exception = new TerminationStopPointCountyNotAllowedException(Country.ALBANIA);
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "A termination workflow is not allowed for the country " + Country.ALBANIA.name());
    assertThat(errorResponse.getError()).isEqualTo(
        "Termination workflow not allowed");
  }

}