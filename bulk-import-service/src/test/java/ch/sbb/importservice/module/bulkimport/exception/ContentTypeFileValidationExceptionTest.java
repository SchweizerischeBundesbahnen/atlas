package ch.sbb.importservice.module.bulkimport.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.importservice.module.bulkimport.exception.ContentTypeFileValidationException;
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