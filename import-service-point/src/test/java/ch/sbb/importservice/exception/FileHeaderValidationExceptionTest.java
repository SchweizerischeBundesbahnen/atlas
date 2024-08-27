package ch.sbb.importservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FileHeaderValidationExceptionTest {

  @Test
  void shouldProvideErrorResponse() {
    FileHeaderValidationException exception = new FileHeaderValidationException();
    String error = exception.getErrorResponse().getError();
    assertThat(error).isEqualTo("File header validation failed");

    String code = exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode();
    assertThat(code).isEqualTo("BULK_IMPORT.ERROR.FILE_HEADER");
  }

}