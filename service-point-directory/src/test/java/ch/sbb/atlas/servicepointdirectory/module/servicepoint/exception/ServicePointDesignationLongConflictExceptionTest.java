package ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicePointDesignationLongConflictExceptionTest {

  @Test
  void shouldHaveCorrectDetailCode() {
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    ServicePointDesignationLongConflictException conflictException = new ServicePointDesignationLongConflictException(
        bernWyleregg, List.of(bernWyleregg));

    assertThat(conflictException.getErrorResponse().getDetails().iterator().next().getDisplayInfo()
        .getCode()).isEqualTo("SEPODI.SERVICE_POINTS.CONFLICT.DESIGNATION_LONG");
  }
}