package ch.sbb.atlas.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SloidNotValidExceptionTest {

  @Test
  void shouldBuildErrorMessageCorrectly() {
    SloidNotValidException exception = new SloidNotValidException("ch.1:sloid:7000:1", "did not start with ch:1:sloid:");
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo(
        "The SLOID ch.1:sloid:7000:1 is not valid due to: did not start with ch:1:sloid:");
  }
}