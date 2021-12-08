package ch.sbb.timetable.field.number.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException {

  private final int status = HttpStatus.BAD_REQUEST.value();
  private final String error = HttpStatus.BAD_REQUEST.getReasonPhrase();
  private final String message;

  public BadRequestException(String message) {
    this.message = message;
  }

}
