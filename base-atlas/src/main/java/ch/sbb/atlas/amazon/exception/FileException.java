package ch.sbb.atlas.amazon.exception;

public class FileException extends RuntimeException {

  public FileException(String message) {
    super(message);
  }

  public FileException(Exception exception) {
    super(exception);
  }

  public FileException(String message, Throwable cause) {
    super(message, cause);
  }

}
