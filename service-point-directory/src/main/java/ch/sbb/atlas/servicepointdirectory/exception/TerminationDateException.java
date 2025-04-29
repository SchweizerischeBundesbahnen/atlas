package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationDateException extends AtlasException {

  private final LocalDate terminationDate;
  private final LocalDate validTo;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.EXPECTATION_FAILED.value())
        .message(getFormattedMsg())
        .error("StopPoint Termination date error")
        .build();
  }

  private String getFormattedMsg() {
    return MessageFormat.format("The termination date {0} must be before service point version validTo {1}",
        terminationDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN)),
        validTo.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN)));
  }

}
