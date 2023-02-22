package ch.sbb.atlas.export.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ExportException extends AtlasException {

  private static final String ERROR = "Export error";

  private final File file;

  private final Exception exception;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("Error exporting file[" + file.getName() + "] to Amazon S3 Bucket: " + exception.getCause())
        .error(ERROR)
        .build();
  }

}
