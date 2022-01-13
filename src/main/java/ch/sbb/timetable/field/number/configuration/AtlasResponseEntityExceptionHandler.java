package ch.sbb.timetable.field.number.configuration;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.exceptions.AtlasException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AtlasResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {AtlasException.class})
  public ResponseEntity<ErrorResponse> atlasException(AtlasException conflictException) {
    return new ResponseEntity<>(conflictException.getErrorResponse(),
        HttpStatus.valueOf(conflictException.getErrorResponse().getHttpStatus()));
  }

}
