package ch.sbb.timetable.field.number.configuration;

import ch.sbb.timetable.field.number.api.ErrorResponseModel;
import ch.sbb.timetable.field.number.exceptions.ConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AtlasResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {ConflictException.class})
  public ResponseEntity<ErrorResponseModel> conflictException(ConflictException conflictException) {
    ErrorResponseModel errorResponseModel = conflictException.toErrorResponse();
    ErrorResponseModel responseModel = ErrorResponseModel
        .builder()
        .errorMessages(errorResponseModel.getErrorMessages())
        .httpStatusCode(errorResponseModel.getHttpStatusCode())
        .build();
    return new ResponseEntity<>(responseModel,
        HttpStatus.valueOf(errorResponseModel.getHttpStatusCode()));
  }

}
