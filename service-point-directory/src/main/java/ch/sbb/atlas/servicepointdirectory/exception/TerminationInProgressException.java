package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationInProgressException extends BaseException {

  public static final String MESSAGE = "StopPoint cannot be edited because a termination is in progress";

  @Override
  protected int getHttpStatus() {
    return HttpStatus.PRECONDITION_FAILED.value();
  }

  @Override
  protected String getCustomMessage() {
    return MESSAGE;
  }

  @Override
  protected String getCustomError() {
    return MESSAGE;
  }

  @Override
  protected SortedSet<Detail> getPreconditionErrorDetails() {
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


