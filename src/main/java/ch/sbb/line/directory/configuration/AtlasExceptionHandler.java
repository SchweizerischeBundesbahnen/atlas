package ch.sbb.line.directory.configuration;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.api.ErrorResponse.DisplayInfo;
import ch.sbb.line.directory.exception.AtlasException;
import ch.sbb.line.directory.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AtlasExceptionHandler {

  @ExceptionHandler(value = {AtlasException.class})
  public ResponseEntity<ErrorResponse> atlasException(AtlasException conflictException) {
    return new ResponseEntity<>(conflictException.getErrorResponse(),
        HttpStatus.valueOf(conflictException.getErrorResponse().getStatus()));
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ResponseEntity<ErrorResponse> notFoundException(NotFoundException notFoundException) {
    return new ResponseEntity<>(notFoundException.getErrorResponse(),
        HttpStatus.valueOf(notFoundException.getErrorResponse().getStatus()));
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> propertyReferenceException(
      PropertyReferenceException exception) {
    log.warn("Pageable sort parameter is not valid.", exception);
    return ResponseEntity.badRequest()
                         .body(ErrorResponse.builder()
                                            .status(HttpStatus.BAD_REQUEST.value())
                                            .error("Property reference error")
                                            .message(
                                                "Supplied sort field " + exception.getPropertyName()
                                                    + " not found on " + exception.getType()
                                                                                  .getType()
                                                                                  .getSimpleName())
                                            .build());
  }

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<ErrorResponse> staleObjectStateException(
      StaleObjectStateException exception) {
    List<Detail> details = List.of(Detail.builder().message(exception.getMessage())
                                         .field("etagVersion")
                                         .displayInfo(DisplayInfo.builder()
                                                                 .code(
                                                                     "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR")
                                                                 .build())
                                         .build());
    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                         .body(ErrorResponse.builder()
                                            .status(
                                                HttpStatus.PRECONDITION_FAILED.value())
                                            .error("Stale object state error")
                                            .message(
                                                exception.getMessage())
                                            .details(details)
                                            .build()
                         );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    List<Detail> details =
        exception.getFieldErrors()
                 .stream()
                 .map(fieldError ->
                     Detail.builder()
                           .field(fieldError.getField())
                           .message("Value {0} rejected due to {1}")
                           .displayInfo(DisplayInfo.builder()
                                                   .code("ERROR.CONSTRAINT")
                                                   .with("rejectedValue",
                                                       String.valueOf(
                                                           fieldError.getRejectedValue()))
                                                   .with("cause", fieldError.getDefaultMessage())
                                                   .build())
                           .build())
                 .collect(Collectors.toList());
    return ResponseEntity.badRequest()
                         .body(ErrorResponse.builder()
                                            .status(HttpStatus.BAD_REQUEST.value())
                                            .error("Method argument not valid error")
                                            .message("Constraint for requestbody was violated")
                                            .details(details)
                                            .build());
  }


}
