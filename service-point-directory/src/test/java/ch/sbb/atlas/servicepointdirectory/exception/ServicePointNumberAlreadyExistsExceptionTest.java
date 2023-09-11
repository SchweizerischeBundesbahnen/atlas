package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;

class ServicePointNumberAlreadyExistsExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    ServicePointNumberAlreadyExistsException exception = new ServicePointNumberAlreadyExistsException(
        ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(409);
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("The service point with number 8507000 already exists.");
  }
}