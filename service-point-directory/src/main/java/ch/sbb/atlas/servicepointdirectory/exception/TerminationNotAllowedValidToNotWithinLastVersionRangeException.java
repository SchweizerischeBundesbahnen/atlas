package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;
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
        .message(getErrorMessage())
        .error("Termination not allowed")
        .details(getErrorDetails())
        .build();
  }

  private String getErrorMessage() {
    return "Termination not allowed for sloid " + sloid + " since the date range for the last version is from "
        + validFromCurrent + " until " + validToCurrent + ". "
        + "And requested validTo value " + requestedValidTo + " is not within the range.";
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("termination")
        .message("Termination not allowed for non latest version.")
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SERVICE_POINTS.TERMINATION_NOT_ALLOWED_FOR_NON_LATEST_VERSION")
            .with("sloid", sloid)
            .build())
        .build());
    return errorDetails;
  }

}
