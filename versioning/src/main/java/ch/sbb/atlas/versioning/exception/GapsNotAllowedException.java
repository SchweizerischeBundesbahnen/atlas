package ch.sbb.atlas.versioning.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class GapsNotAllowedException extends AtlasException {

  private static final String MESSAGE = "Gaps not allowed after versioning";

  @Override
  public ErrorResponse getErrorResponse() {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(
        Detail.builder()
            .message(MESSAGE)
            .displayInfo(DisplayInfo.builder().code("ERROR.GAPS_NOT_ALLOWED").build())
            .build());

    return ErrorResponse.builder()
        .message(MESSAGE)
        .status(HttpStatus.BAD_REQUEST.value())
        .error(MESSAGE)
        .details(details)
        .build();
  }
}
