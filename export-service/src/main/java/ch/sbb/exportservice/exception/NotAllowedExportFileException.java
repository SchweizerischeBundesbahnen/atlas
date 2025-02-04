package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAllowedExportFileException extends AtlasException {

  private final SePoDiBatchExportFileName exportFileName;
  private final ExportTypeBase exportTypeBase;

  public NotAllowedExportFileException(SePoDiBatchExportFileName exportFileName, SePoDiExportType exportTypeBase) {
    this.exportFileName = exportFileName;
    this.exportTypeBase = exportTypeBase;
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Download file [" + exportFileName + "] with export type [" + exportTypeBase + "] not allowed!")
        .error("To download the file [" + exportFileName + "] are only allowed the following export types: "
            + SePoDiExportType.getWorldOnly())
        .build();
  }
}
