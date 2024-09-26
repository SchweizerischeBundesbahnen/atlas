package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class NotAPdfDocumentExceptionTest {

  @Test
  void shouldProvideCorrectFrontendCode() {
    NotAPdfDocumentException notAPdfDocumentException = new NotAPdfDocumentException(List.of("file.pdf"));
    assertThat(notAPdfDocumentException.getErrorResponse().getDetails().first().getDisplayInfo().getCode())
        .isEqualTo("COMMON.FILEUPLOAD.ERROR.NOT_A_PDF");
  }
}