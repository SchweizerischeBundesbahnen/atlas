package ch.sbb.timetable.field.number.configuration;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.api.ErrorResponse.Detail;
import ch.sbb.timetable.field.number.api.ErrorResponse.DisplayInfo;
import ch.sbb.timetable.field.number.exceptions.AtlasException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AtlasExceptionHandler {

  @ExceptionHandler(value = {AtlasException.class})
  public ResponseEntity<ErrorResponse> atlasException(AtlasException conflictException) {
    return new ResponseEntity<>(conflictException.getErrorResponse(),
        HttpStatus.valueOf(conflictException.getErrorResponse().getHttpStatus()));
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> propertyReferenceException(
      PropertyReferenceException exception) {
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

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<ErrorResponse> staleObjectStateException(StaleObjectStateException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        ErrorResponse.builder()
            .httpStatus(HttpStatus.CONFLICT.value())
            .message(exception.getMessage())
            .details(List.of(Detail.builder()
                .message(exception.getMessage())
                .field("")
                .displayInfo(DisplayInfo.builder()
                    .with("entityName", exception.getEntityName())
                    .code("COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR")
                    .build())
                .build()))
            .build()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    List<Detail> details = exception.getFieldErrors()
        .stream()
        .map(toErrorDetail())
        .collect(Collectors.toList());
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder()
            .httpStatus(HttpStatus.BAD_REQUEST.value())
            .message("Constraint for requestbody was violated")
            .details(details)
            .build());
  }

  private Function<FieldError, Detail> toErrorDetail() {
    return fieldError -> Detail.builder()
        .field(fieldError.getField())
        .message("Value {0} rejected due to {1}")
        .displayInfo(DisplayInfo.builder()
            .code("TTFN.CONSTRAINT")
            .with("rejectedValue", String.valueOf(
                fieldError.getRejectedValue()))
            .with("cause",
                fieldError.getDefaultMessage()).build())
        .build();
  }
}
