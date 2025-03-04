package ch.sbb.exportservice.exception;

import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotAllowedExportFileExceptionTest {

  @Test
  void shouldGetNotAllowedExportFileException() {
    // given

    // when
    NotAllowedExportFileException exception = Assertions.assertThrows(NotAllowedExportFileException.class, () -> {
      throw new NotAllowedExportFileException(SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION,
          SePoDiExportType.SWISS_ONLY_FULL);
    });

    // then
    assertThat(exception.getErrorResponse().getError()).isEqualTo(
        "To download the file [TRAFFIC_POINT_ELEMENT_VERSION] are only allowed the following export types: ["
            + "SWISS_ONLY_FULL, SWISS_ONLY_ACTUAL, SWISS_ONLY_TIMETABLE_FUTURE, WORLD_FULL, WORLD_ONLY_ACTUAL, WORLD_ONLY_TIMETABLE_FUTURE]");
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(400);
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo(
        "Download file [TRAFFIC_POINT_ELEMENT_VERSION] with export type [SWISS_ONLY_FULL] not allowed!");

  }

}
