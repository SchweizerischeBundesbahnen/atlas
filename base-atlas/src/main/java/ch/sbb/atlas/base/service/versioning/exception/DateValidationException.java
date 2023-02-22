package ch.sbb.atlas.base.service.versioning.exception;

public class DateValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DateValidationException(String message) {
    super(message);
  }
}
