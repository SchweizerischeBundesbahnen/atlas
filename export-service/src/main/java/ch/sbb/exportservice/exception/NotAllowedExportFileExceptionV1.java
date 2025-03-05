package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;

@Deprecated(forRemoval = true)
public class NotAllowedExportFileExceptionV1 extends AtlasException {

  private final Enum<?> type;
  private final Class<?> subType;

  public NotAllowedExportFileExceptionV1(Enum<?> type, Class<?> subType) {
    this.type = type;
    this.subType = subType;
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Download file [" + type + "] with unsupported export type!")
        .error("To download the file [" + type + "] are only allowed the following export types: "
            + Arrays.toString(subType.getEnumConstants()))
        .build();
  }

}
