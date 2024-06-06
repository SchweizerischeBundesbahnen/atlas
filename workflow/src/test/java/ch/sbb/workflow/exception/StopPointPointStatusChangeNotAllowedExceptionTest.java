package ch.sbb.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import org.junit.jupiter.api.Test;

class StopPointPointStatusChangeNotAllowedExceptionTest {

  private final StopPointPointStatusChangeNotAllowedException exception = new StopPointPointStatusChangeNotAllowedException(
      WorkflowStatus.ADDED,WorkflowStatus.APPROVED);

  @Test
  void shouldDisplayConflictCorrectly() {
    assertThat(exception.getErrorResponse().getStatus()).isEqualTo(412);
    assertThat(exception.getErrorResponse().getMessage()).isEqualTo("Stop Point Workflow Status cannot be changed from ADDED to "
        + "APPROVED!");
  }

}