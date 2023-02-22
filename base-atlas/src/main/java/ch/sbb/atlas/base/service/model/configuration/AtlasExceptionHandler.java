package ch.sbb.atlas.base.service.model.configuration;

import ch.sbb.atlas.base.service.model.api.ErrorResponse;
import ch.sbb.atlas.base.service.model.api.ErrorResponse.Detail;
import ch.sbb.atlas.base.service.model.api.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.base.service.model.exception.AtlasException;
import ch.sbb.atlas.base.service.model.exception.NotFoundException;
import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import ch.sbb.atlas.base.service.versioning.exception.VersioningNoChangesException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.hibernate.StaleObjectStateException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class AtlasExceptionHandler {

  @Autowired
  private ObjectMapper objectMapper;

  @ExceptionHandler(value = VersioningNoChangesException.class)
  public ResponseEntity<ErrorResponse> versioningNoChangesException(
      VersioningNoChangesException ex) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(
        Detail.builder()
            .message(ex.getMessage())
            .displayInfo(DisplayInfo.builder().code("ERROR.WARNING.VERSIONING_NO_CHANGES").build())
            .build());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(ex.getMessage())
        .status(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS)
        .error("No changes after versioning")
        .details(details)
        .build();
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(value = VersioningException.class)
  public ResponseEntity<ErrorResponse> versioningException(
      VersioningException versioningException) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(
        Detail.builder()
            .message(versioningException.getMessage())
            .displayInfo(DisplayInfo.builder().code("ERROR.VERSIONING").build())
            .build());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(versioningException.getMessage())
        .status(HttpStatus.NOT_IMPLEMENTED.value())
        .error("Versioning scenario not implemented")
        .details(details)
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
  }

  @ExceptionHandler(value = MultipartException.class)
  public ResponseEntity<ErrorResponse> multipartException(
      MultipartException multipartException) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(
        Detail.builder()
            .message(multipartException.getMessage())
            .displayInfo(DisplayInfo.builder().code("ERROR.MULTIPART").build())
            .build());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .message(multipartException.getMessage())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("No multipartFile provided")
        .details(details)
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
  }

  @ExceptionHandler(value = {AtlasException.class})
  public ResponseEntity<ErrorResponse> atlasException(AtlasException conflictException) {
    ErrorResponse errorResponse = conflictException.getErrorResponse();
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ResponseEntity<ErrorResponse> notFoundException(
      NotFoundException liDiNotFoundException) {
    ErrorResponse errorResponse = liDiNotFoundException.getErrorResponse();
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> propertyReferenceException(
      PropertyReferenceException exception) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Property reference error")
            .message("Supplied sort field " + exception.getPropertyName()
                + " not found on " + exception.getType()
                .getType()
                .getSimpleName())
            .build());
  }

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<ErrorResponse> staleObjectStateException(
      StaleObjectStateException exception) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(
        Detail.builder().message(exception.getMessage())
            .field("etagVersion")
            .displayInfo(DisplayInfo.builder()
                .code("COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR")
                .build())
            .build());
    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
        .body(ErrorResponse.builder()
            .status(
                HttpStatus.PRECONDITION_FAILED.value())
            .error("Stale object state error")
            .message(exception.getMessage())
            .details(details)
            .build()
        );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    SortedSet<Detail> details = exception.getFieldErrors()
        .stream()
        .map(fieldError ->
            Detail.builder()
                .field(fieldError.getField())
                .message("Value {0} rejected due to {1}")
                .displayInfo(DisplayInfo.builder()
                    .code("ERROR.CONSTRAINT")
                    .with("rejectedValue", String.valueOf(fieldError.getRejectedValue()))
                    .with("cause", fieldError.getDefaultMessage())
                    .build())
                .build())
        .collect(Collectors.toCollection(TreeSet::new));
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Method argument not valid error")
            .message("Constraint for requestbody was violated")
            .details(details)
            .build());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    Set<String> messages = new HashSet<>(constraintViolations.size());
    messages.addAll(constraintViolations.stream()
        .map(
            constraintViolation -> String.format(
                "Path parameter '%s' value '%s' %s",
                ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().getName(),
                constraintViolation.getInvalidValue(),
                constraintViolation.getMessage())).toList());

    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Param argument not valid error")
            .message("Constraint for Path parameter was violated: " + messages)
            .build());
  }

  @ExceptionHandler(value = FeignException.class)
  public ResponseEntity<ErrorResponse> handleFeignException(FeignException feignException) throws IOException {
    if (feignException.responseBody().isPresent()) {
      String responseBodyContent = new String(feignException.responseBody().get().array());
      ErrorResponse response = objectMapper.readValue(responseBodyContent, ErrorResponse.class);
      return ResponseEntity.status(feignException.status()).body(response);
    }
    throw new UnsupportedOperationException();
  }
}
