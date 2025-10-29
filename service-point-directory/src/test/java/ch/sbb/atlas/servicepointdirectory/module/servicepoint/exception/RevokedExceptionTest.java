package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RevokedExceptionTest {

  @Test
  void shouldHaveCorrectDisplayCode() {
    RevokedException exception = new RevokedException("ch:1:sloid:7000");
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo("COMMON.REVOKED");
  }
}