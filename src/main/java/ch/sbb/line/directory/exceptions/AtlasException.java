package ch.sbb.line.directory.exceptions;

import ch.sbb.line.directory.api.ErrorResponse;

public abstract class AtlasException extends RuntimeException {

  public abstract ErrorResponse getErrorResponse();

}
