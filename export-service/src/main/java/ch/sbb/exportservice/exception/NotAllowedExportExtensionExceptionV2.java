package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportObjectV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class NotAllowedExportExtensionExceptionV2 extends AtlasException {

  private final ExportObjectV2 exportObject;
  private final ExportExtensionFileType exportExtensionFileType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Download file [" + exportObject + "] with unsupported export extension [" + exportExtensionFileType + "]!")
        .error("To download the file [" + exportObject + "] as [" + exportExtensionFileType + "] is not supported. Supported "
            + "extensions: " + exportObject.getSupportedExtensionFileTypes())
        .build();
  }

}
