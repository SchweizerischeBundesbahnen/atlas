package ch.sbb.atlas.servicepointdirectory.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import org.junit.jupiter.api.Test;

class StopPointNotLocatedInSwitzerlandExceptionTest {

  @Test
  void shouldDisplayErrorMessage(){
    // given
    StopPointNotLocatedInSwitzerlandException exception = new StopPointNotLocatedInSwitzerlandException("ch:1:sloid:8000");
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("The provided ServicePoint with sloid: ch:1:sloid:8000 is not a StopPoint Located in Switzerland!");
  }

}