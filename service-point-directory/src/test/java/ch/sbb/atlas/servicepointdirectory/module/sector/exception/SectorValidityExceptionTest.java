package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SectorValidityExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    SectorValidityException exception = new SectorValidityException(DateRange.builder()
        .from(LocalDate.of(2020,1,1))
        .to(LocalDate.of(2025,1,1))
        .build());

    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("Business rule validation failed");
    assertThat(errorResponse.getError()).isEqualTo("Validity is not in range of validity of traffic point");

    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo("SEPODI.SECTORS.TRAFFIC_POINT_VALIDITY_ERROR");
  }
}
