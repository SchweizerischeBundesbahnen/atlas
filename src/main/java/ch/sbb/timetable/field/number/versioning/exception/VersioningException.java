package ch.sbb.timetable.field.number.versioning.exception;

public class VersioningException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public VersioningException(String message) {
    super(message);
  }

  public VersioningException(String message, Throwable error) {
    super(message, error);
  }
}
