package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import org.junit.jupiter.api.Test;

class TerminationDecisionPersonExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    TerminationDecisionPerson infoPlus = TerminationDecisionPerson.INFO_PLUS;
    TerminationDecisionPersonException exception = new TerminationDecisionPersonException(infoPlus);
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(428);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "TerminationDecisionPerson must be " + infoPlus);
  }

}