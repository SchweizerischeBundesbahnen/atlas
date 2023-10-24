package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class StopPointDoesNotExistsException extends AtlasException {

  private final String sloid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message("The stop place with sloid " + getSloid() + " does not exists.")
        .build();
  }

}
