package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SePoDiClientException extends AtlasException {

 private final ErrorResponse errorResponse;

  @Override
  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }

}
