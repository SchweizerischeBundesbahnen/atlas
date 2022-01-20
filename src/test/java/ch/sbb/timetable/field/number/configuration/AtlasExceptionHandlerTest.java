package ch.sbb.timetable.field.number.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.sbb.timetable.field.number.api.ErrorResponse;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class AtlasExceptionHandlerTest {

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
    assertThat(errorResponseResponseEntity.getBody()).isNotNull();
    assertThat(errorResponseResponseEntity.getBody().getHttpStatus()).isEqualTo(
        HttpStatus.BAD_REQUEST.value());
    assertThat(errorResponseResponseEntity.getBody().getMessage()).isEqualTo(
        "Constraint for requestbody was violated");
    assertThat(errorResponseResponseEntity.getBody().getDetails()).size().isEqualTo(1);
    assertThat(errorResponseResponseEntity.getBody().getDetails().get(0).getMessage()).isEqualTo(
        "Value null rejected due to defaultMessage");
    assertThat(errorResponseResponseEntity.getBody()
                                          .getDetails()
                                          .get(0)
                                          .getDisplayInfo()
                                          .getCode()).isEqualTo("TTFN.CONSTRAINT");
  }
}