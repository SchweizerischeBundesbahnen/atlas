package ch.sbb.atlas.base.service.model.exception;


import ch.sbb.atlas.base.service.model.api.ErrorResponse;

public abstract class AtlasException extends RuntimeException {

  public abstract ErrorResponse getErrorResponse();

}
