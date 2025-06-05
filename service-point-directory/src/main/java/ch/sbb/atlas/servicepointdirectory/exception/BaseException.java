package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ErrorResponseBuilder;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class BaseException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    ErrorResponseBuilder responseBuilder = ErrorResponse.builder()
        .status(getHttpStatus())
        .message(getCustomMessage())
        .error(getCustomError());
    if (getPreconditionErrorDetails() != null) {
      responseBuilder.details(getPreconditionErrorDetails());
    }
    return responseBuilder
        .build();
  }

  protected abstract int getHttpStatus();

  protected abstract String getCustomMessage();

  protected abstract String getCustomError();

  protected SortedSet<Detail> getPreconditionErrorDetails() {
    //Override me if you want!!!
    return new TreeSet<>();
  }

}
