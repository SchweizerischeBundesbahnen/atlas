package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAllowedExportFileException extends AtlasException {

  private final BatchExportFileName exportFileName;
  private final SePoDiExportType sePoDiExportType;

  public NotAllowedExportFileException(BatchExportFileName exportFileName, SePoDiExportType sePoDiExportType) {
    this.exportFileName = exportFileName;
    this.sePoDiExportType = sePoDiExportType;
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Download file [" + exportFileName + "] with export type [" + sePoDiExportType + "] not allowed!")
        .error("To download the file [" + exportFileName + "] are only allowed the following export types: "
            + SePoDiExportType.getWorldOnly())
        .build();
  }
}
