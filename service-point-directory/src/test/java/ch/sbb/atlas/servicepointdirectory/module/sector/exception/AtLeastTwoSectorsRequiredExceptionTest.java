package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class AtLeastTwoSectorsRequiredExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    AtLeastTwoSectorsRequiredException exception = new AtLeastTwoSectorsRequiredException();
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("At least two sector's are required");
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "SEPODI.SECTORS.REQUIRED_AT_LEAST_TWO_SECTOR_ERROR");
  }
}
