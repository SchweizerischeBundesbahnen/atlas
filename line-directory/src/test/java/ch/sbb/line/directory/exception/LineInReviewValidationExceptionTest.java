package ch.sbb.line.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineInReviewValidationExceptionTest {

    @Test
    void shouldMapToErrorResponse() {
        ErrorResponse errorResponse = new LineInReviewValidationException().getErrorResponse();
        assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.UPDATE_IN_REVIEW");
    }
}