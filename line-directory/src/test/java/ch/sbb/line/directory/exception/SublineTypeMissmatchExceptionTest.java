package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import org.junit.jupiter.api.Test;

class SublineTypeMissmatchExceptionTest {

  @Test
  void shouldHaveCorrectCode() {
    SublineTypeMissmatchException exception = new SublineTypeMissmatchException(SublineType.CONCESSION, LineType.TEMPORARY);
    assertThat(exception.getErrorResponse().getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.SUBLINE.TYPE.MISSMATCH");
  }
}