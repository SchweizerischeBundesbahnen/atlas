package ch.sbb.importservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.importservice.entity.BulkImport;
import org.junit.jupiter.api.Test;

class LogFileNotFoundExceptionTest {

  @Test
  void shouldHaveDisplayCode() {
    LogFileNotFoundException exception = new LogFileNotFoundException(BulkImport.builder().id(5L).build());
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Logfile for import with id 5 not found");
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("BULK_IMPORT.ERROR.LOGFILE_NOT_FOUND");
  }
}