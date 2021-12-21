package ch.sbb.timetable.field.number.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonIgnoreProperties(value = {"stackTrace"})
public class BadRequestException extends RuntimeException {

  private final int status = HttpStatus.BAD_REQUEST.value();
  private final String error = HttpStatus.BAD_REQUEST.getReasonPhrase();

  public BadRequestException(String message) {
    super(message);
  }

}
