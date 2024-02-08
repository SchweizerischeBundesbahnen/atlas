package ch.sbb.atlas.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SloidAlreadyExistsExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    SloidAlreadyExistsException exception = new SloidAlreadyExistsException("ch:1:sloid:7000:0:455");
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("The SLOID ch:1:sloid:7000:0:455 is already in use.");
    assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo().getCode()).isEqualTo(
        "SEPODI.SLOID_ALREADY_USED");
  }
}
