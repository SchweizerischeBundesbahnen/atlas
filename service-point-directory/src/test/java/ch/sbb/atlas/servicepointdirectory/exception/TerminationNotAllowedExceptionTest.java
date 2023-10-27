package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;

class TerminationNotAllowedExceptionTest {

  @Test
  void shouldHaveCorrectDetailCode() {
    TerminationNotAllowedException exception = new TerminationNotAllowedException(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));

    assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo()
        .getCode()).isEqualTo("SEPODI.SERVICE_POINTS.TERMINATION_FORBIDDEN");
  }
}