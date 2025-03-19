package ch.sbb.exportservice.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import org.junit.jupiter.api.Test;

class NotAllowedExportFileExceptionV2Test {

  @Test
  void getErrorResponse() {
    // given
    // when
    final NotAllowedExportFileExceptionV2 exception = assertThrows(NotAllowedExportFileExceptionV2.class,
        () -> {
          throw new NotAllowedExportFileExceptionV2(ExportObjectV2.LINE, ExportTypeV2.FULL);
        });
    // then
    final ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("Download file [LINE] with unsupported export type [FULL]!");
    assertThat(errorResponse.getError()).isEqualTo(
        "To download the file [LINE] are only allowed the following export types: [FULL, ACTUAL, FUTURE_TIMETABLE, TIMETABLE_YEARS]");
  }

}
