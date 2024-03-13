package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class PlatformAlreadyExistExceptionTest {

    @Test
    void shouldPrintErrorMessage(){
        //when
        PlatformAlreadyExistsException result = new PlatformAlreadyExistsException("ch:1:sloid:18771:1");

        //then
        ErrorResponse errorResponse = result.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorResponse.getMessage()).isEqualTo("The platform with sloid ch:1:sloid:18771:1 already exists.");
    }
}
