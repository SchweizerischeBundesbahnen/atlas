package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationNotAllowedValidToNotWithinLastVersionRangeException extends AtlasException {

  private final String sloid;
  private final LocalDate requestedValidTo;
  private final LocalDate validFromCurrent;
  private final LocalDate validToCurrent;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("Termination not allowed for sloid " + sloid + " since the date range for the last version is from "
            + validFromCurrent + " until " + validToCurrent + ". "
            + "And requested validTo value " + requestedValidTo + " is not within the range.")
        .error("Termination not allowed")
        .build();
  }
}
