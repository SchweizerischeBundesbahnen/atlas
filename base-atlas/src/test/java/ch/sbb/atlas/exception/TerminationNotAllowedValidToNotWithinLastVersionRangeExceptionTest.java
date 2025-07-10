package ch.sbb.atlas.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TerminationNotAllowedValidToNotWithinLastVersionRangeExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    String sloid = "sloid";
    LocalDate from = LocalDate.of(2024, 1, 1);
    LocalDate to = LocalDate.of(2024, 12, 31);
    LocalDate validTo = LocalDate.of(2025, 1, 1);
    DateRange dateRange = new DateRange(from, to);

    TerminationNotAllowedValidToNotWithinLastVersionRangeException exception =
        new TerminationNotAllowedValidToNotWithinLastVersionRangeException(
            sloid, validTo, dateRange.getFrom(),
            dateRange.getTo());
    assertThat(exception.getErrorResponse().getError()).isEqualTo("Termination not allowed");
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
  }
}
