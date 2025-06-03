package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationInProgressException extends AtlasException {

  public static final String MESSAGE = "StopPoint cannot be edited because a termination is in progress";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message(MESSAGE)
        .error(MESSAGE)
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .field("termination")
        .message(MESSAGE)
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SERVICE_POINTS.TERMINATION_IN_PROGRESS")
            .build())
        .build());
    return errorDetails;
  }

}


