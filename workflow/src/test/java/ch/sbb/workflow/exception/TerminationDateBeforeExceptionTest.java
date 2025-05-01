package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TerminationDateBeforeExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    final LocalDate givenTerminationDate = LocalDate.of(2020, 1, 1);
    final LocalDate currentTerminationDate = LocalDate.of(2022, 1, 1);
    TerminationDateBeforeException exception = new TerminationDateBeforeException(givenTerminationDate, currentTerminationDate);
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "The given termination date 2020-01-01 cannot be before the current termination date 2022-01-01!");
  }
}