package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateRangeConflictExceptionTest {

  @Test
  void shouldDisplayExeptionCorrectly() {
    DateRange dateRangeMainline = new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1));

    DateRangeConflictException exception = new DateRangeConflictException(dateRangeMainline);
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.CONFLICT"
        + ".SUBLINE_VALIDITY");
  }
}