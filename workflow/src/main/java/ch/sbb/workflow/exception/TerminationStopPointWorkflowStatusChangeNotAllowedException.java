package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationStopPointWorkflowStatusChangeNotAllowedException extends AtlasException {

  private final TerminationWorkflowStatus actualWorkflowStatus;
  private final TerminationWorkflowStatus currentWorkflowStatus;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(MessageFormat.format(
            "Termination Stop Point Workflow Status cannot be changed from {0} to {1}!", actualWorkflowStatus,
            currentWorkflowStatus))
        .error("Update status not allowed!")
        .build();
  }

}
