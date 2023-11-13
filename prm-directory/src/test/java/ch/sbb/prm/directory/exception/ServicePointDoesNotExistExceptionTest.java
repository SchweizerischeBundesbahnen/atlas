package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ServicePointDoesNotExistExceptionTest {

  @Test
  void shouldPrintErrorMessage(){
    //when
    ServicePointDoesNotExistException result = new ServicePointDoesNotExistException("ch:1:sloid:8000");

    //then
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getMessage()).isEqualTo("The service point with sloid ch:1:sloid:8000 does not exist.");
  }

}