package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import java.util.SortedSet;
import org.junit.jupiter.api.Test;

class HeightNotCalculatableExceptionTest {

  private final HeightNotCalculatableException exception = new HeightNotCalculatableException();

  @Test
  void shouldInformAboutUnavailableHeight() {
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(400);
    SortedSet<Detail> details = exception.getErrorResponse().getDetails();
    assertThat(details).hasSize(1);
    assertThat(details.iterator().next().getDisplayInfo().getCode()).isEqualTo(
        "SEPODI.SERVICE_POINTS.HEIGHT_SERVICE_UNAVAILABLE");
  }
}