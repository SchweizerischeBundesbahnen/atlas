package ch.sbb.atlas.workflow.exception;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import org.junit.jupiter.api.Test;

class WorkflowObjectStatusExceptionTest {

  @Test
  void shouldHaveDisplayCode() {
    WorkflowObjectStatusException exception = new WorkflowObjectStatusException(Status.DRAFT);
    assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo("WORKFLOW.ERROR.WORKFLOW_OBJECT_STATUS_MUST_BE_DRAFT");
  }
}