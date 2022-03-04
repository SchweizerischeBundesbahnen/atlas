package ch.sbb.atlas.versioning.exception;

public class DateValidationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DateValidationException(String message) {
    super(message);
  }
}
