package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RevokedExceptionTest {

  @Test
  void shouldHaveDisplayCode() {
    RevokedException exception = new RevokedException("ch:1:slnid:234");
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.ERROR.REVOKED");
  }
}