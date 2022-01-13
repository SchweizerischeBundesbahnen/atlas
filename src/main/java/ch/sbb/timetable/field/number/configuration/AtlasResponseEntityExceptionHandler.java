package ch.sbb.timetable.field.number.configuration;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.exceptions.AtlasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class AtlasResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {AtlasException.class})
  public ResponseEntity<ErrorResponse> atlasException(AtlasException conflictException) {
    return new ResponseEntity<>(conflictException.getErrorResponse(),
        HttpStatus.valueOf(conflictException.getErrorResponse().getHttpStatus()));
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> handleInvalidSort(PropertyReferenceException exception) {
    log.warn("Pageable sort parameter is not valid.", exception);
    return ResponseEntity.badRequest()
                         .body(ErrorResponse.builder()
                                            .httpStatus(HttpStatus.BAD_REQUEST.value())
                                            .message(
                                                "Supplied sort field " + exception.getPropertyName()
                                                    + " not found on " + exception.getType()
                                                                                  .getType()
                                                                                  .getSimpleName())
                                            .build());
  }
}
