package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import java.text.MessageFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class StopPointPointStatusChangeNotAllowedException extends AtlasException {

  private final WorkflowStatus actualWorkflowStatus;
  private final WorkflowStatus currentWorkflowStatus;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(MessageFormat.format(
            "Stop Point Workflow Status cannot be changed from {0} to {1}!", actualWorkflowStatus, currentWorkflowStatus))
        .error("Update status not allowed!")
        .build();
  }

}
