package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class LoadingPointNumberAlreadyExistsException extends AtlasException {

  private final ServicePointNumber servicePointNumber;
  private final Integer number;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("The loading point with number " + number + " already exists for service point with number "
            + servicePointNumber.getNumber())
        .build();
  }

}
