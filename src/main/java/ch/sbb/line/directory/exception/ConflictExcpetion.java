package ch.sbb.line.directory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConflictExcpetion extends ResponseStatusException {

  public static final String SWISS_NUMBER_NOT_UNIQUE_MESSAGE = "SwissNumber already taken in specified period";
  public static final String TEMPORARY_LINE_VALIDITY_TOO_LONG_MESSAGE = "Validity of temporary line is longer than 12 months";

  public ConflictExcpetion(String message) {
    super(HttpStatus.CONFLICT, message);
  }

}
