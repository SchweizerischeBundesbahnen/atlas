package ch.sbb.exportservice.exception;

public class CsvException extends RuntimeException {

  public CsvException(String message) {
    super(message);
  }

  public CsvException(Exception exception) {
    super(exception);
  }

}
