package ch.sbb.atlas.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.versioning.exception.DateOrderException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DateOrderExceptionTest {

    @Test
    void shouldDisplayErrorMessageMinRange(){
        // given
        DateOrderException exception = new DateOrderException("Edited ValidFrom is bigger than edited ValidTo",
                LocalDate.of(2300,1,1), LocalDate.of(2000,1,1));

        // when & then
        ErrorResponse errorResponse = exception.getErrorResponse();
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getMessage()).isEqualTo("Edited ValidFrom is bigger than edited ValidTo");
        assertThat(errorResponse.getError()).isEqualTo("Edited ValidFrom is bigger than edited ValidTo");
        DisplayInfo displayInfo = exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo();

        assertThat(displayInfo.getParameters()).hasSize(2);
        assertThat(displayInfo
                .getCode()).isEqualTo("VALIDATION.DATE_ORDER_ERROR");

        Parameter onlyParameter = displayInfo.getParameters().getFirst();
        assertThat(onlyParameter.getKey()).isEqualTo("validFrom");
        assertThat(onlyParameter.getValue()).isEqualTo("01.01.2300");

        Parameter secondParameter = displayInfo.getParameters().get(1);
        assertThat(secondParameter.getKey()).isEqualTo("validTo");
        assertThat(secondParameter.getValue()).isEqualTo("01.01.2000");
    }
}
