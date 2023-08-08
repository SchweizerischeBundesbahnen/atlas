package ch.sbb.exportservice.exception;

import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotAllowedExportFileExceptionTest {

    @Test
    public void shouldGetNotAllowedExportFileException() {
        // given

        // when
        NotAllowedExportFileException exception = Assertions.assertThrows(NotAllowedExportFileException.class, () -> {
            throw new NotAllowedExportFileException(ExportFileName.TRAFFIC_POINT_ELEMENT_VERSION, ExportType.SWISS_ONLY_FULL);
        });
        // then
        assertThat(exception.getErrorResponse().getError()).isEqualTo("To download the file [TRAFFIC_POINT_ELEMENT_VERSION] are only allowed the following export types: [WORLD_FULL, WORLD_ONLY_ACTUAL, WORLD_ONLY_TIMETABLE_FUTURE]");
        assertThat(exception.getErrorResponse().getStatus()).isEqualTo(400);
        assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Download file [TRAFFIC_POINT_ELEMENT_VERSION] with export type [SWISS_ONLY_FULL] not allowed!");

    }

}