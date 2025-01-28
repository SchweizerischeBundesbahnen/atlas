package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidFreightServicePointExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    InvalidFreightServicePointException exception = new InvalidFreightServicePointException();
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "SEPODI.SERVICE_POINTS.ERROR.INVALID_FREIGHT_SERVICE_POINT");
  }
}