package ch.sbb.atlas.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.versioning.exception.DateValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateValidationExceptionTest {
    @Test
    void shouldDisplayErrorMessage(){
        // given
        DateValidationException exception = new DateValidationException("ValidTo cannot be after 31.12.9999.");
        // when & then
        ErrorResponse errorResponse = exception.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getMessage()).isEqualTo("ValidTo cannot be after 31.12.9999.");
        assertThat(errorResponse.getError()).isEqualTo("ValidTo cannot be after 31.12.9999.");
        assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo()
                .getCode()).isEqualTo("VALIDATION.DATE_RANGE_ERROR");
    }
}
