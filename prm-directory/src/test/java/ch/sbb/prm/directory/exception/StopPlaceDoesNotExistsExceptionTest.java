package ch.sbb.prm.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class StopPlaceDoesNotExistsExceptionTest {

  @Test
  void shouldPrintErrorMessage(){
    //when
    StopPlaceDoesNotExistsException result = new StopPlaceDoesNotExistsException("ch:1:sloid:8000");

    //then
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getMessage()).isEqualTo("The stop place with sloid ch:1:sloid:8000 does not exists.");
  }

}