package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SePoDiClientWrongStatusReturnedException extends AtlasException {

  private final Status expectedStatus;
  private final Status returnedStatus;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.EXPECTATION_FAILED.value())
        .message(MessageFormat.format("Expected Stop Point is {0} instead of {1}!",expectedStatus,returnedStatus))
        .error("Returned wrong Stop Point Status from SePoDi Client!")
        .build();
  }
}
