package ch.sbb.atlas.configuration.handler;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.model.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class AtlasExceptionHandler {

  @Autowired
  private ObjectMapper objectMapper;

  @ExceptionHandler(value = MultipartException.class)
  public ResponseEntity<ErrorResponse> multipartException(MultipartException multipartException) {
    ErrorResponse errorResponse = ErrorResponseMapper.map(multipartException);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(value = {AtlasException.class})
  public ResponseEntity<ErrorResponse> atlasException(AtlasException atlasException) {
    ErrorResponse errorResponse = ErrorResponseMapper.map(atlasException);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ResponseEntity<ErrorResponse> notFoundException(NotFoundException notFoundException) {
    ErrorResponse errorResponse = ErrorResponseMapper.map(notFoundException);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ErrorResponse> propertyReferenceException(PropertyReferenceException exception) {
    return ResponseEntity.badRequest().body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<ErrorResponse> staleObjectStateException(StaleObjectStateException exception) {
    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
    return ResponseEntity.badRequest().body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
    return ResponseEntity.badRequest().body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
    return ResponseEntity.badRequest().body(ErrorResponseMapper.map(e));
  }

  @ExceptionHandler(value = FeignException.class)
  public ResponseEntity<ErrorResponse> handleFeignException(FeignException feignException) throws IOException {
    Optional<ByteBuffer> responseBody = feignException.responseBody();
    if (responseBody.isPresent()) {
      String responseBodyContent = new String(responseBody.get().array());
      ErrorResponse response = objectMapper.readValue(responseBodyContent, ErrorResponse.class);
      return ResponseEntity.status(feignException.status()).body(response);
    }
    throw new UnsupportedOperationException();
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class, MissingServletRequestPartException.class})
  public ResponseEntity<ErrorResponse> handleBadRequestExceptionsException(Exception exception) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(exception.getMessage())
            .message(exception.getMessage())
            .build());
  }

  @ExceptionHandler(value = ClientAbortException.class)
  public void handleException(ClientAbortException exception) {
    log.debug("Client aborted connection", exception);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(value = NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException exception) {
    return ResponseEntity.badRequest().body(ErrorResponseMapper.map(exception));
  }

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    if (exception instanceof AsyncRequestNotUsableException) {
      log.debug("Client aborted the connection", exception);
      return ResponseEntity.status(499).build();
    }
    log.error("Unexpected Exception occurred", exception);
    return ResponseEntity.internalServerError().body(ErrorResponseMapper.map(exception));
  }

}
