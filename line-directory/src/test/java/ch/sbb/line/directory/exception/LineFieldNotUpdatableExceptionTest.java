package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import org.junit.jupiter.api.Test;

class LineFieldNotUpdatableExceptionTest {

  @Test
  void shouldMapError() {
    LineFieldNotUpdatableException exception = new LineFieldNotUpdatableException("newVersion", "oldVersion",
        LineType.DISPOSITION);
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "LIDI.LINE.CONFLICT.NON_UPDATABLE");
  }

}