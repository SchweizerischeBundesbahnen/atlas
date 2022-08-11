package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.model.api.ErrorResponse;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ExportException extends AtlasException {

  private static final String ERROR = "Export error";

  private final File file;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Error exporting file[" + file.getName() + "] to Amazon S3 Bucket")
                        .error(ERROR)
                        .build();
  }

}
