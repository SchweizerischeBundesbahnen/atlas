package ch.sbb.exportservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Arrays;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotAllowedExportFileException extends AtlasException { // todo: new type combination validation for

  private final Enum<?> type;
  private final Enum<?> subType;

  public NotAllowedExportFileException(Enum<?> type, Enum<?> subType) {
    this.type = type;
    this.subType = subType;
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Download file [" + type + "] with export type [" + subType + "] not allowed!")
        .error("To download the file [" + type + "] are only allowed the following export types: "
            + Arrays.toString(subType.getDeclaringClass().getEnumConstants())) // todo: replace ...
        .build();
  }
}
