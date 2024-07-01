package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationNotAllowedWhenVersionInReviewException extends AtlasException {

  private final ServicePointNumber servicePointNumber;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message(getErrorMessage())
        .error("Termination not allowed")
        .details(getErrorDetails())
        .build();
  }

  private static String getErrorMessage() {
    return "Termination not allowed when a version is in  " + Status.IN_REVIEW;
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("termination")
        .message(getErrorMessage())
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SERVICE_POINTS.TERMINATION_NOT_ALLOWED_WITH_VERSION_IN_REVIEW")
            .with("number", String.valueOf(servicePointNumber.getNumber()))
            .build())
        .build());
    return errorDetails;
  }
}


