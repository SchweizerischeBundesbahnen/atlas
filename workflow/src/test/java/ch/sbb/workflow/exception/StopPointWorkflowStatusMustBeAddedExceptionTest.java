package ch.sbb.workflow.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StopPointWorkflowStatusMustBeAddedExceptionTest {

    @Test
    void shouldHaveCorrectErrorCode() {
        StopPointWorkflowStatusMustBeAddedException exception = new StopPointWorkflowStatusMustBeAddedException();
        assertThat(exception.getErrorResponse().getDetails().getFirst().getDisplayInfo().getCode()).isEqualTo(
                "WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_ADDED");
    }
}
