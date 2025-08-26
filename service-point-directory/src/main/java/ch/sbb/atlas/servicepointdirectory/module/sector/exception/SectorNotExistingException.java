package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SectorNotExistingException extends AtlasException {

  private final String sloid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message("No sector exists with sloid " + sloid)
        .build();
  }

}
