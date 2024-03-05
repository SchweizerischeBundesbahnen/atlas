package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.Parameter;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;

class ServicePointNumberAlreadyExistsExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    ServicePointNumberAlreadyExistsException exception = new ServicePointNumberAlreadyExistsException(
        ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(409);
    assertThat(errorResponse.getMessage()).isEqualTo("The service point with number 8507000 already exists.");
    assertThat(errorResponse.getError()).isEqualTo("Service Point number already exists");

    Detail detail = errorResponse.getDetails().first();
    assertThat(detail.getField()).isEqualTo("number");
    assertThat(detail.getMessage()).isEqualTo("Service Point with number 85 07000 is already existing.");

    DisplayInfo displayInfo = detail.getDisplayInfo();
    assertThat(displayInfo.getCode()).isEqualTo("SEPODI.NUMBER_ALREADY_USED");
    assertThat(displayInfo.getParameters()).hasSize(1);

    Parameter onlyParameter = displayInfo.getParameters().getFirst();
    assertThat(onlyParameter.getKey()).isEqualTo("number");
    assertThat(onlyParameter.getValue()).isEqualTo("85 07000");
  }

}
