package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationNotOnLastVersionException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.EXPECTATION_FAILED.value())
        .message(getFormattedMsg())
        .error("StopPoint Termination error")
        .build();
  }

  private String getFormattedMsg() {
    return "The StopPoint version is not the oldest version: "
        + "termination is only possible on the oldest of StopPoint version";
  }
}
