package ch.sbb.atlas.model.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.export.exception.ExportException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExportExceptionTest {

  @Test
  void shouldGetExportException() throws IOException {
    //given
    File file = Files.createTempFile("file", ".csv").toFile();
    //when
    ExportException exception = Assertions.assertThrows(ExportException.class, () -> {
      throw new ExportException(file, new Exception());
    });

    //then
    assertThat(exception.getErrorResponse().getError()).isEqualTo("Export error");
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(500);
    assertThat(exception.getErrorResponse().getMessage()).contains(
        "Error exporting file[");

  }

}