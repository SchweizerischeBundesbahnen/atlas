package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CoordinatesNotTransformableExceptionTest {

  @Test
  void shouldHaveCorrectDetailCode() {
    CoordinatesNotTransformableException exception =
        new CoordinatesNotTransformableException(new IllegalStateException("Laditude not in range"));

    assertThat(exception.getErrorResponse().getDetails().iterator().next().getDisplayInfo()
        .getCode()).isEqualTo("SEPODI.GEOLOCATION.INVALID");
  }

}