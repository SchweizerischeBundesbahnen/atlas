package ch.sbb.atlas.workflow.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class WorkflowObjectStatusException extends AtlasException {

  private final Status expectedObjectStatus;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_REQUIRED.value())
        .message("Object status must be " + expectedObjectStatus)
        .error("Workflow error - specific object status required")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Wrong status")
        .field("status")
        .displayInfo(builder().code("WORKFLOW.ERROR.WORKFLOW_OBJECT_STATUS_MUST_BE_" + expectedObjectStatus.name()).build())
        .build());
  }
}
