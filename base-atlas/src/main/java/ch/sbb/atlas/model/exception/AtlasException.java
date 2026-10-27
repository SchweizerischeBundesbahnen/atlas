package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import java.io.Serializable;

public abstract class AtlasException extends RuntimeException implements Serializable {

  protected AtlasException() {
  }

  protected AtlasException(Throwable cause) {
    super(cause);
  }

  public abstract ErrorResponse getErrorResponse();

  @Override
  public String getMessage() {
    return getErrorResponse().getMessage();
  }

}
