package ch.sbb.atlas.configuration.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.versioning.exception.VersioningNoChangesException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Collections;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

public class AtlasExceptionHandlerTest {

  private final AtlasExceptionHandler atlasExceptionHandler = new AtlasExceptionHandler(new ObjectMapper());

  @Test
  void shouldConvertMethodArgumentExceptionToErrorResponse() {
    // Given
    BeanPropertyBindingResult bindingResult = mock(BeanPropertyBindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(
        Collections.singletonList(new FieldError("objectName", "field", "defaultMessage")));
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
        mock(MethodParameter.class), bindingResult);

    // When
    ResponseEntity<ErrorResponse> errorResponseEntity = atlasExceptionHandler.methodArgumentNotValidException(
        exception);

    // Then
    assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ErrorResponse responseBody = errorResponseEntity.getBody();
    assertThat(responseBody).isNotNull();
    assertThat(responseBody.getStatus()).isEqualTo(
        HttpStatus.BAD_REQUEST.value());
    assertThat(responseBody.getMessage()).isEqualTo(
        "Constraint for requestbody was violated");
    assertThat(responseBody.getDetails()).size().isEqualTo(1);
    assertThat(responseBody.getDetails().first().getMessage()).isEqualTo(
        "Value null rejected due to defaultMessage");
    assertThat(responseBody.getDetails()
        .first()
        .getDisplayInfo()
        .getCode()).isEqualTo("ERROR.CONSTRAINT");
  }

  @Test
  void shouldConvertBadRequestsExceptionToErrorResponse() {
    // Given
    MissingServletRequestPartException exception = new MissingServletRequestPartException("file");

    // When
    ResponseEntity<ErrorResponse> errorResponseEntity = atlasExceptionHandler.handleBadRequestExceptionsException(exception);

    // Then
    assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ErrorResponse responseBody = errorResponseEntity.getBody();
    assertThat(responseBody).isNotNull();
    assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(responseBody.getMessage()).isEqualTo("Required part 'file' is not present.");
  }

  @Test
  void shouldConvertUnexpectedExceptionToErrorResponse() {
    // Given
    StaleStateException exception = new StaleStateException("bad things happened");

    // When
    ResponseEntity<ErrorResponse> errorResponseEntity = atlasExceptionHandler.handleException(exception);

    // Then
    assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    ErrorResponse responseBody = errorResponseEntity.getBody();
    assertThat(responseBody).isNotNull();
    assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(responseBody.getMessage()).isEqualTo("bad things happened");
  }

  @Test
  void shouldIgnoreClientAbortException() {
    assertDoesNotThrow(() -> atlasExceptionHandler.handleException(new ClientAbortException()));
  }

  @Test
  void shouldConvertAccessDeniedExceptionToErrorResponse() {
    // Given
    AccessDeniedException exception = new AccessDeniedException("Access Denied");

    // When
    ResponseEntity<ErrorResponse> errorResponseEntity = atlasExceptionHandler.handleAccessDeniedException(
        exception);

    // Then
    assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    ErrorResponse responseBody = errorResponseEntity.getBody();
    assertThat(responseBody).isNotNull();
    assertThat(responseBody.getStatus()).isEqualTo(
        HttpStatus.FORBIDDEN.value());
    assertThat(responseBody.getMessage()).isEqualTo(
        "You are not allowed to perform this operation on the ATLAS platform.");
    assertThat(responseBody.getDetails()).size().isEqualTo(1);
    assertThat(responseBody.getDetails().first().getMessage()).isEqualTo(
        "Access Denied");
    assertThat(responseBody.getDetails()
        .first()
        .getDisplayInfo()
        .getCode()).isEqualTo("ERROR.NOTALLOWED");
  }

  @Test
  void shouldConvertMethodArgumentTypeMismatchExceptionToErrorResponse() {
    // Given
    MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException("falseValue", ExportType.class,
        "exportType", mock(MethodParameter.class), new IllegalArgumentException());

    // When
    ResponseEntity<ErrorResponse> errorResponseEntity = atlasExceptionHandler.methodArgumentTypeMismatchException(exception);

    // Then
    assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ErrorResponse responseBody = errorResponseEntity.getBody();
    assertThat(responseBody).isNotNull();
    assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(responseBody.getMessage()).isEqualTo("Method argument type did not match expected value range");

    assertThat(responseBody.getDetails()).size().isEqualTo(1);
    assertThat(responseBody.getDetails().first().getMessage()).isEqualTo("Value falseValue could not be converted to ExportType");

    DisplayInfo displayInfo = responseBody.getDetails().first().getDisplayInfo();
    assertThat(displayInfo.getCode()).isEqualTo("ERROR.CONSTRAINT");
    assertThat(displayInfo.getParameters().get(0).getValue()).isEqualTo("falseValue");
    assertThat(displayInfo.getParameters().get(1).getValue()).isEqualTo("ExportType");
    assertThat(displayInfo.getParameters().get(2).getValue()).isEqualTo("[FULL, ACTUAL_DATE, FUTURE_TIMETABLE]");
  }

  @Test
  void shouldHandleConnectionAbortsGracefully() {
    // Given
    AsyncRequestNotUsableException exception = new AsyncRequestNotUsableException("Client abort");

    // When
    ResponseEntity<ErrorResponse> errorResponseEntity = atlasExceptionHandler.handleException(exception);

    // Then
    assertThat(errorResponseEntity.getStatusCode().value()).isEqualTo(499);
  }

  @Test
  void shouldMapAtlasExceptionToErrorResponse() {
    ErrorResponse errorResponse = atlasExceptionHandler.mapToErrorResponse(new SloidNotFoundException("ch:1:sloid:12333"));
    assertThat(errorResponse.getMessage()).isEqualTo("Entity not found");
  }

  @Test
  void shouldMapVersioningNoChangesExceptionToErrorResponse() {
    ErrorResponse errorResponse = atlasExceptionHandler.mapToErrorResponse(new VersioningNoChangesException());
    assertThat(errorResponse.getMessage()).isEqualTo("No entities were modified after versioning execution.");
  }

  @Test
  void shouldMapConstraintViolationExceptionToErrorResponse() {
    ErrorResponse errorResponse = atlasExceptionHandler.mapToErrorResponse(getExampleConstraintViolation());

    assertThat(errorResponse.getDetails()).size().isEqualTo(2);
  }

  private ConstraintViolationException getExampleConstraintViolation() {
    UpdateServicePointVersionModel servicePointVersionModel = UpdateServicePointVersionModel.builder()
        .designationOfficial("BernZuLangBernZuLangBernZuLangBernZuLangBernZuLang")
        .businessOrganisation("ch:1:sboid:5846489645")
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    return new ConstraintViolationException(validator.validate(servicePointVersionModel));
  }

  @Test
  void shouldMapAccessDeniedExceptionToErrorResponse() {
    ErrorResponse errorResponse = atlasExceptionHandler.mapToErrorResponse(new AccessDeniedException("no"));
    assertThat(errorResponse.getMessage()).isEqualTo("You are not allowed to perform this operation on the ATLAS platform.");
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo("ERROR.NOTALLOWED");
  }

}
