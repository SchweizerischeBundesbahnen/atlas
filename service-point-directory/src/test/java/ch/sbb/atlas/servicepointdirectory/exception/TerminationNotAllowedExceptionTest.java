package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.junit.jupiter.api.Test;

class TerminationNotAllowedExceptionTest {

  @Test
  void shouldHaveCorrectDetailCode() {
    ServicePointVersion servicePointVersion = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .build();
    TerminationNotAllowedException exception = new TerminationNotAllowedException(servicePointVersion);

    assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo()
        .getCode()).isEqualTo("SEPODI.SERVICE_POINTS.TERMINATION_FORBIDDEN");
  }
}