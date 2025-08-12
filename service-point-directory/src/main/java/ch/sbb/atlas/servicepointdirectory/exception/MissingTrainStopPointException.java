package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import org.springframework.http.HttpStatus;

public class MissingTrainStopPointException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Expected at least one service point version that is a stop point and has only TRAIN as means of "
            + "transport.")
        .build();
  }
}
