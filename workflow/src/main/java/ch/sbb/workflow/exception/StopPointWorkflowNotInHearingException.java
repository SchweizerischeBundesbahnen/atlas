package ch.sbb.workflow.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class StopPointWorkflowNotInHearingException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("The stop point workflow is not in hearing")
        .error("StopPoint Workflow error")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Pin code not valid")
        .field("workflowStatus")
        .displayInfo(builder()
            .code("WORKFLOW.ERROR.NOT_IN_HEARING")
            .build())
        .build());
  }

}
