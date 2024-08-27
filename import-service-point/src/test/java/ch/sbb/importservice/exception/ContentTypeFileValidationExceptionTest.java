package ch.sbb.importservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeFileValidationExceptionTest {

  private final ContentTypeFileValidationException contentTypeFileValidationException = new ContentTypeFileValidationException(
      "application/json");

  @Test
  void shouldProvideErrorResponse() {
    String error = contentTypeFileValidationException.getErrorResponse().getError();
    assertThat(error).isEqualTo("ContentType application/json not supported");
  }
}