package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ObjectRevokedException extends AtlasException {

  private final Class<? extends BasePrmEntityVersion> clazz;
  private final String sloid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message("The " + getClazz().getSimpleName() + " with sloid " + getSloid() + " is revoked. Updates are not allowed.")
        .build();
  }

}
