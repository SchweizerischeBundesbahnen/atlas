package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class OrderlyLineValidityExceptionTest {


  @Test
  void shouldHaveCorrectDisplayMessage() {
    OrderlyLineValidityException orderlyLineValidityException = new OrderlyLineValidityException(
        LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 16));

    assertThat(orderlyLineValidityException.getErrorResponse().getDetails().first().getDisplayInfo().getCode())
        .isEqualTo("LIDI.LINE.ORDERLY_LINE_MIN_VALIDITY");
    assertThat(orderlyLineValidityException.getErrorResponse().getDetails().first().getDisplayInfo().getParameters())
        .hasSize(2);
  }
}