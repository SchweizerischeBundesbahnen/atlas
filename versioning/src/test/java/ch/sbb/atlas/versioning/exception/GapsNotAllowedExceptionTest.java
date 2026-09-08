package ch.sbb.atlas.versioning.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GapsNotAllowedExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    GapsNotAllowedException exception = new GapsNotAllowedException();
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("ERROR.GAPS_NOT_ALLOWED");
  }
}