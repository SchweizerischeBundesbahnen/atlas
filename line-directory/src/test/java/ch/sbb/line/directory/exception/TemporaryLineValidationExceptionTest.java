package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TemporaryLineValidationExceptionTest {

  @Test
  void shouldHaveCorrectDisplayMessage() {
    TemporaryLineValidationException temporaryLineValidationException = new TemporaryLineValidationException(
        LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 16));

    assertThat(temporaryLineValidationException.getErrorResponse().getDetails().first().getDisplayInfo().getCode())
        .isEqualTo("LIDI.LINE.TEMPORARY_VERSION_EXCEEDS_MAX_VALIDITY");
    assertThat(temporaryLineValidationException.getErrorResponse().getDetails().first().getDisplayInfo().getParameters())
        .hasSize(2);
  }

}
