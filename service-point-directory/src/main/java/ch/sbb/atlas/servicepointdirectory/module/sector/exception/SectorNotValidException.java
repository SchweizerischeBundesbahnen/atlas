package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SectorNotValidException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("At least two sector's are required")
        .build();
  }

}
