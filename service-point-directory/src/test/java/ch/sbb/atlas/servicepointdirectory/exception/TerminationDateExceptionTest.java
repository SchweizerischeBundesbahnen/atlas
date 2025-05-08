package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TerminationDateExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    final LocalDate terminationDate = LocalDate.of(2020, 1, 1);
    final LocalDate validTo = LocalDate.of(2022, 1, 1);
    TerminationDateException exception = new TerminationDateException(terminationDate, validTo);
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "The termination date 2020-01-01 must be before service point version validTo 2022-01-01");
  }

}