package ch.sbb.atlas.versioning.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class VersioningException extends AtlasException {

  private final String cause;

  public VersioningException() {
    this("Something went wrong. I'm not able to apply versioning.");
  }

  public VersioningException(String message) {
    this.cause = message;
  }

  @Override
  public ErrorResponse getErrorResponse() {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(
        Detail.builder()
            .message(cause)
            .displayInfo(DisplayInfo.builder().code("ERROR.VERSIONING").build())
            .build());

    return ErrorResponse.builder()
        .message(cause)
        .status(HttpStatus.NOT_IMPLEMENTED.value())
        .error("Versioning scenario not implemented")
        .details(details)
        .build();
  }
}
