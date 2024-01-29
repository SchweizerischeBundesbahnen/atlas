package ch.sbb.atlas.servicepoint;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class SloidNotValidException extends AtlasException {

  private final String sloid;
  private final String reason;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("The SLOID " + sloid + " is not valid due to: " + reason)
        .error("SLOID not valid")
        .build();
  }

}
