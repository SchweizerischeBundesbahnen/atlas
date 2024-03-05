package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ElementTypeDoesNotExistException extends AtlasException {

  private final String sloid;
  private final String element;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message("The " + getElement() + " with sloid " + getSloid() + " does not exist.")
        .build();
  }

}
