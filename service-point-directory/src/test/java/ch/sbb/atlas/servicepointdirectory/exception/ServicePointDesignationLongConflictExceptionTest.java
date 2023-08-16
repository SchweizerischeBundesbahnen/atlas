package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
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