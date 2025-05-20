package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidFareStopExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    InvalidFareStopException invalidFareStopException = new InvalidFareStopException();
    assertThat(invalidFareStopException.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "SEPODI.SERVICE_POINTS.ERROR.INVALID_FARE_STOP");
  }
}