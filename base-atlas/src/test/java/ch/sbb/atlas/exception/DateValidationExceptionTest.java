package ch.sbb.atlas.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.versioning.exception.DateValidationException;
import ch.sbb.atlas.versioning.exception.DateValidationException.ValidationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DateValidationExceptionTest {

    @Test
    void shouldDisplayErrorMessageMaxRange(){
        // given
        DateValidationException exception = new DateValidationException("ValidTo cannot be after: ", LocalDate.of(9999,12,31), ValidationType.MAX);
        // when & then
        ErrorResponse errorResponse = exception.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getMessage()).isEqualTo("ValidTo cannot be after: 9999-12-31");
        assertThat(errorResponse.getError()).isEqualTo("ValidTo cannot be after: 9999-12-31");
        DisplayInfo displayInfo = exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo();

        assertThat(displayInfo.getParameters()).hasSize(1);
        assertThat(displayInfo
                .getCode()).isEqualTo("VALIDATION.MATDATEPICKERMAX");

        Parameter onlyParameter = displayInfo.getParameters().getFirst();
        assertThat(onlyParameter.getKey()).isEqualTo("date");
        assertThat(onlyParameter.getValue()).isEqualTo("31.12.9999");
    }

    @Test
    void shouldDisplayErrorMessageMinRange(){
        // given
        DateValidationException exception = new DateValidationException("ValidFrom cannot be before: ", LocalDate.of(1700,1,1), ValidationType.MIN);
        // when & then
        ErrorResponse errorResponse = exception.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getMessage()).isEqualTo("ValidFrom cannot be before: 1700-01-01");
        assertThat(errorResponse.getError()).isEqualTo("ValidFrom cannot be before: 1700-01-01");
        DisplayInfo displayInfo = exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo();

        assertThat(displayInfo.getParameters()).hasSize(1);
        assertThat(displayInfo
                .getCode()).isEqualTo("VALIDATION.MATDATEPICKERMIN");

        Parameter onlyParameter = displayInfo.getParameters().getFirst();
        assertThat(onlyParameter.getKey()).isEqualTo("date");
        assertThat(onlyParameter.getValue()).isEqualTo("01.01.1700");
    }
}
