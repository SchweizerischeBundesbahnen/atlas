package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class BadRequestException extends AtlasException {

  private final String message;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(message)
        .error(message)
        .build();
  }

}
