package ch.sbb.atlas.configuration.handler;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@UtilityClass
public class ErrorResponseMapper {

  public static ErrorResponse map(AtlasException exception) {
    return exception.getErrorResponse();
  }

  public static ErrorResponse map(AccessDeniedException exception) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(Detail.builder()
        .message(exception.getMessage())
        .displayInfo(
            DisplayInfo.builder()
                .code("ERROR.NOTALLOWED")
                .build()
        )
        .build());
    return ErrorResponse.builder().status(HttpStatus.FORBIDDEN.value())
        .message(
            "You are not allowed to perform this operation on the ATLAS platform.")
        .error("Access denied")
        .details(details).build();
  }

  public static ErrorResponse map(MethodArgumentNotValidException exception) {
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
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Method argument not valid error")
        .message("Constraint for requestbody was violated")
        .details(details)
        .build();
  }

  public static ErrorResponse map(ConstraintViolationException exception) {
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    ConstraintViolationMapper constraintViolationMapper = new ConstraintViolationMapper(constraintViolations);
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error(
            "Param argument not valid on: " + constraintViolations.stream().findFirst().map(ConstraintViolation::getLeafBean))
        .message(constraintViolationMapper.getMessage())
        .details(constraintViolationMapper.getDetails())
        .build();
  }

  public static ErrorResponse map(PropertyReferenceException exception) {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Property reference error")
        .message("Supplied sort field " + exception.getPropertyName()
            + " not found on " + exception.getType()
            .getType()
            .getSimpleName())
        .build();
  }

  public static ErrorResponse map(StaleObjectStateException exception) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(Detail.builder()
        .message(exception.getMessage())
        .field("etagVersion")
        .displayInfo(DisplayInfo.builder()
            .code("COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR")
            .build())
        .build());
    return ErrorResponse.builder()
        .status(
            HttpStatus.PRECONDITION_FAILED.value())
        .error("Stale object state error")
        .message(exception.getMessage())
        .details(details)
        .build();
  }

  public static ErrorResponse map(MethodArgumentTypeMismatchException exception) {
    SortedSet<Detail> details = new TreeSet<>();
    Class<?> requiredType = Objects.requireNonNull(exception.getRequiredType());
    details.add(Detail.builder()
        .field(exception.getName())
        .message("Value {0} could not be converted to {1}")
        .displayInfo(DisplayInfo.builder()
            .code("ERROR.CONSTRAINT")
            .with("rejectedValue", String.valueOf(exception.getValue()))
            .with("expectedType", requiredType.getSimpleName())
            .with("allowedEnumValues", Arrays.toString(requiredType.getEnumConstants()))
            .build())
        .build());
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Method argument type not valid error")
        .message("Method argument type did not match expected value range")
        .details(details)
        .build();
  }

  public static ErrorResponse map(NoResourceFoundException exception) {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .error(exception.getMessage())
        .message(exception.getMessage())
        .build();
  }

  public static ErrorResponse map(HttpRequestMethodNotSupportedException exception) {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error(exception.getMessage())
        .message(exception.getMessage())
        .build();
  }

  public static ErrorResponse map(MultipartException exception) {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(Detail.builder()
        .message(exception.getMessage())
        .displayInfo(DisplayInfo.builder().code("ERROR.MULTIPART").build())
        .build());

    return ErrorResponse.builder()
        .message(exception.getMessage())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("No multipartFile provided")
        .details(details)
        .build();
  }

  public static ErrorResponse map(Exception exception) {
    return ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(exception.getMessage())
        .message(exception.getMessage())
        .build();
  }

  public ErrorResponse mapToErrorResponse(Exception exception) {
    if (exception instanceof AtlasException atlasException) {
      return map(atlasException);
    }
    if (exception instanceof AccessDeniedException accessDeniedException) {
      return map(accessDeniedException);
    }
    if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
      return map(methodArgumentNotValidException);
    }
    if (exception instanceof ConstraintViolationException constraintViolationException) {
      return map(constraintViolationException);
    }
    return map(exception);
  }
}
