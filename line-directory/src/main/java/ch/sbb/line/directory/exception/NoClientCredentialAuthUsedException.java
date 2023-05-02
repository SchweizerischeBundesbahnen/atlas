package ch.sbb.line.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import org.springframework.http.HttpStatus;

public class NoClientCredentialAuthUsedException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Bad authentication used")
        .error("For this endpoint the clientCredential Authentication should be used")
        .build();
  }

}
