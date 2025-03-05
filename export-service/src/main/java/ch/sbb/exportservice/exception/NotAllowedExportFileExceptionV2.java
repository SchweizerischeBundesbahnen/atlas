package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class NotAllowedExportFileExceptionV2 extends AtlasException {

  private final ExportObjectV2 exportObject;
  private final ExportTypeV2 exportType;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Download file [" + exportObject + "] with unsupported export type [" + exportType + "]!")
        .error("To download the file [" + exportObject + "] are only allowed the following export types: "
            + exportObject.getSupportedExportTypes())
        .build();
  }

}
