package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationNotAllowedWhenVersionNotLastException extends AtlasException {

  private final String sloid;
  private final Long id;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("Termination not allowed for sloid " + sloid + " since the version with the given id " + id +
            " is not the last version. Termination is only allowed for the last version.")
        .error("Termination not allowed")
        .build();
  }
}
