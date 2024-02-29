package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class ElementTypeDoesNotExistExceptionTest {

    @Test
    void shouldPrintErrorMessage(){
        //when
        ElementTypeDoesNotExistException result = new ElementTypeDoesNotExistException("ch:1:sloid:8000", "PLATFORM");

        //then
        ErrorResponse errorResponse = result.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
        assertThat(errorResponse.getMessage()).isEqualTo("The PLATFORM with sloid ch:1:sloid:8000 does not exist.");
    }
}
