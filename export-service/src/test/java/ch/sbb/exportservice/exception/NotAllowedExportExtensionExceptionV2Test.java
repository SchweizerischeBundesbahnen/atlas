package ch.sbb.exportservice.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportObjectV2;
import org.junit.jupiter.api.Test;

class NotAllowedExportExtensionExceptionV2Test {

  @Test
  void getErrorResponse() {
    // given
    // when
    final NotAllowedExportExtensionExceptionV2 exception = assertThrows(NotAllowedExportExtensionExceptionV2.class,
        () -> {
          throw new NotAllowedExportExtensionExceptionV2(ExportObjectV2.SERVICE_POINT, ExportExtensionFileType.XML_EXTENSION);
        });
    // then
    final ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Download file [SERVICE_POINT] with unsupported export extension [XML_EXTENSION]!");
    assertThat(errorResponse.getError()).isEqualTo(
        "To download the file [SERVICE_POINT] as [XML_EXTENSION] is not supported. Supported extensions: []");
  }

}
