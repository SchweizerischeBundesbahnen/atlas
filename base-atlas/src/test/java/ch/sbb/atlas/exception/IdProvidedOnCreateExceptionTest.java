package ch.sbb.atlas.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IdProvidedOnCreateExceptionTest {

  @Test
  void shouldHaveCorrectErrorCode() {
    IdProvidedOnCreateException exception = new IdProvidedOnCreateException();
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Create of element not allowed when ID is provided.");
  }
}
