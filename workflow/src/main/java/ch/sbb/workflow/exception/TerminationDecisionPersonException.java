package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationDecisionPersonException extends AtlasException {

  private final TerminationDecisionPerson expectedTerminationDecisionPerson;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_REQUIRED.value())
        .message("TerminationDecisionPerson must be " + expectedTerminationDecisionPerson)
        .error("Workflow error")
        .build();
  }

}
