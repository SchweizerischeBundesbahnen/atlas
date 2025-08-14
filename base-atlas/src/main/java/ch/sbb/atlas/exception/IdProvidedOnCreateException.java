package ch.sbb.atlas.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import org.springframework.http.HttpStatus;

public class IdProvidedOnCreateException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Create of element not allowed when ID is provided.")
        .error("Create of element not allowed when ID is provided.")
        .build();
  }
}
