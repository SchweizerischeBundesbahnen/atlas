package ch.sbb.line.directory.module.tth.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import org.springframework.http.HttpStatus;

public class TimetableHearingMovedException extends AtlasException {

  private static final String NEW_PATH = "/timetable-hearing/v2/timetable-hearing/statements/external";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.GONE.value())
        .message("This endpoint moved to " + NEW_PATH)
        .error("Operation not allowed")
        .build();
  }

}
