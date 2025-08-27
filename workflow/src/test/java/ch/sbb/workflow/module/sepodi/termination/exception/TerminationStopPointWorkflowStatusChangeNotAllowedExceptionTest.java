package ch.sbb.workflow.module.sepodi.termination.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import org.junit.jupiter.api.Test;

class TerminationStopPointWorkflowStatusChangeNotAllowedExceptionTest {

  @Test
  void shouldDisplayErrorMessage() {
    // given
    TerminationStopPointWorkflowStatusChangeNotAllowedException exception =
        new TerminationStopPointWorkflowStatusChangeNotAllowedException(TerminationWorkflowStatus.STARTED,
            TerminationWorkflowStatus.TERMINATION_NOT_APPROVED_CLOSED);
    // when & then
    ErrorResponse errorResponse = exception.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Termination Stop Point Workflow Status cannot be changed from STARTED to TERMINATION_NOT_APPROVED_CLOSED!");
    assertThat(errorResponse.getError()).isEqualTo("Update status not allowed!");
  }

}