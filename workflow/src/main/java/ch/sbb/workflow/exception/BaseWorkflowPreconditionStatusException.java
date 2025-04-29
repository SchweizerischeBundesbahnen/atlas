package ch.sbb.workflow.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow.Fields;
import java.util.List;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public abstract class BaseWorkflowPreconditionStatusException extends AtlasException {

  protected abstract String getExpectedWorkflowStatus();

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_REQUIRED.value())
        .message("Workflow status must be " + getExpectedWorkflowStatus())
        .error("StopPoint Workflow error")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Wrong status")
        .field(Fields.status)
        .displayInfo(builder()
            .code("WORKFLOW.ERROR.WORKFLOW_STATUS_MUST_BE_" + getExpectedWorkflowStatus())
            .with(Fields.status, getExpectedWorkflowStatus())
            .build())
        .build());
  }
}

