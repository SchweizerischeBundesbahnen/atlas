package ch.sbb.atlas.s3.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FileExceptionTest {

  @Test
  void shouldCreateFileExceptionFromException() {
    // Given
    Exception cause = new Exception("original error");

    // When
    FileException fileException = new FileException(cause);

    // Then
    assertThat(fileException.getCause()).isEqualTo(cause);
  }

  @Test
  void shouldCreateFileExceptionWithMessageAndCause() {
    // Given
    Throwable cause = new Throwable("root cause");

    // When
    FileException fileException = new FileException("message", cause);

    // Then
    assertThat(fileException.getMessage()).isEqualTo("message");
    assertThat(fileException.getCause()).isEqualTo(cause);
  }

}
