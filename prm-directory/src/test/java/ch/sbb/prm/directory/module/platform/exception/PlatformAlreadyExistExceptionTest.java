package ch.sbb.prm.directory.module.platform.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PlatformAlreadyExistExceptionTest {

  @Test
  void shouldPrintErrorMessage() {
    //when
    PlatformAlreadyExistsException result = new PlatformAlreadyExistsException("ch:1:sloid:18771:1");

    //then
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(errorResponse.getMessage()).isEqualTo("The platform with sloid ch:1:sloid:18771:1 already exists.");
  }
}
