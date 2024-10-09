package ch.sbb.atlas.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
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
        DisplayInfo displayInfo = exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo();

        assertThat(displayInfo.getParameters()).hasSize(1);
        assertThat(displayInfo
                .getCode()).isEqualTo("VALIDATION.DATE_RANGE_ERROR");

        Parameter onlyParameter = displayInfo.getParameters().getFirst();
        assertThat(onlyParameter.getKey()).isEqualTo("date");
        assertThat(onlyParameter.getValue()).isEqualTo("Date: 31.12.9999");
    }

    @Test
    void shouldDisplayErrorMessageWithDateRange(){
        // given
        DateValidationException exception = new DateValidationException("Edited ValidFrom 31.12.9999 is bigger than edited ValidTo 01.01.2000");
        // when & then
        ErrorResponse errorResponse = exception.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getMessage()).isEqualTo("Edited ValidFrom 31.12.9999 is bigger than edited ValidTo 01.01.2000");
        assertThat(errorResponse.getError()).isEqualTo("Edited ValidFrom 31.12.9999 is bigger than edited ValidTo 01.01.2000");
        DisplayInfo displayInfo = exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo();

        assertThat(displayInfo.getParameters()).hasSize(1);
        assertThat(displayInfo
                .getCode()).isEqualTo("VALIDATION.DATE_RANGE_ERROR");

        Parameter onlyParameter = displayInfo.getParameters().getFirst();
        assertThat(onlyParameter.getKey()).isEqualTo("date");
        assertThat(onlyParameter.getValue()).isEqualTo("Valid From: 31.12.9999, Valid To: 01.01.2000");
    }
}
