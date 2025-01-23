package ch.sbb.line.directory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class LineTypeOrderlyExceptionTest {

  @Test
  void shouldMapToErrorResponse() {
    ErrorResponse errorResponse = new LineTypeOrderlyException(LineType.ORDERLY).getErrorResponse();
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo("LIDI.LINE.ERROR.MANDATORY");
  }

}