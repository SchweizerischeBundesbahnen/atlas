package ch.sbb.atlas.base.service.versioning.exception;

public class VersioningException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public VersioningException() {
    super("Something went wrong. I'm not able to apply versioning.");
  }

  public VersioningException(String message) {
    super(message);
  }

  public VersioningException(String message, Throwable error) {
    super(message, error);
  }
}
