package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationStopPointWorkflowConflictException extends AtlasException {

  private static final String TERMINATION_WORKFLOW_IS_ALREADY_IN_PROGRESS = "A termination workflow is in progress!";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message(TERMINATION_WORKFLOW_IS_ALREADY_IN_PROGRESS)
        .error(TERMINATION_WORKFLOW_IS_ALREADY_IN_PROGRESS)
        .build();
  }
}
