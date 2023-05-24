package ch.sbb.atlas.model.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.configuration.handler.AtlasExceptionHandler;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class AtlasExceptionHandlerTest {

 private final AtlasExceptionHandler atlasExceptionHandler = new AtlasExceptionHandler();
  @Test
  void shouldConvertMethodArgumentExceptionToErrorResponse() {
    // Given
    BeanPropertyBindingResult bindingResult = mock(BeanPropertyBindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(
        Collections.singletonList(new FieldError("objectName", "field", "defaultMessage")));
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
        mock(MethodParameter.class), bindingResult);

    // When
    ResponseEntity<ErrorResponse> errorResponseResponseEntity = atlasExceptionHandler.methodArgumentNotValidException(
        exception);

    // Then
    assertThat(errorResponseResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ErrorResponse responseBody = errorResponseResponseEntity.getBody();
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
}
