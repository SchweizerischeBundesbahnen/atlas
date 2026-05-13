package ch.sbb.atlas.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class NotFoundValidVersionForToday extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("No valid version for today found.")
        .error("No valid version for today found.")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .message("No valid version for today found.")
        .build());
    return errorDetails;
  }
}
