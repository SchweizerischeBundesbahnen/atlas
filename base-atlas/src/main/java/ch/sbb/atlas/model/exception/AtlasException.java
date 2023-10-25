package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.api.model.ErrorResponse;

public abstract class AtlasException extends RuntimeException {

  public abstract ErrorResponse getErrorResponse();

  @Override
  public String getMessage(){
    return getErrorResponse().getMessage();
  }

}
