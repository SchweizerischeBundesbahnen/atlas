package ch.sbb.workflow.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow.Fields;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class StopPointWorkflowStatusException extends AtlasException {

  private final WorkflowStatus expectedWorkflowStatus;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_REQUIRED.value())
        .message("Workflow status must be " + expectedWorkflowStatus)
        .error("StopPoint Workflow error")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Wrong status")
        .field(Fields.status)
        .displayInfo(builder()
            .code("WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_" + expectedWorkflowStatus.name())
            .with(Fields.status, expectedWorkflowStatus.name())
            .build())
        .build());
  }
}
