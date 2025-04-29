package ch.sbb.workflow.exception;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationDateBeforeException extends AtlasException {

  private final LocalDate givenTerminationDate;
  private final LocalDate currentTerminationDate;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.EXPECTATION_FAILED.value())
        .message(getFormattedMsg())
        .error("StopPoint Workflow error")
        .build();
  }

  private String getFormattedMsg() {
    return MessageFormat.format("The given termination date {0} cannot be before the current termination date {1}!",
        givenTerminationDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN)),
        currentTerminationDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN)));
  }
}
