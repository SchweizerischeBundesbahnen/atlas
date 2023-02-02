package ch.sbb.importservice.exception;

public class FileException extends RuntimeException {

  public FileException(String message) {
    super(message);
  }

  public FileException(Exception exception) {
    super(exception);
  }

}
