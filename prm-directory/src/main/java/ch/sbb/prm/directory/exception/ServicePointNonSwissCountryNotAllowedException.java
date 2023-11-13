package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ServicePointNonSwissCountryNotAllowedException extends AtlasException {

  private final ServicePointNumber servicePointNumber;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message("PRM does not allow to create StopPoints from non-Swiss ServicePoints!")
        .error("The given ServicePointNumber " + servicePointNumber.getNumber() + " has " + servicePointNumber.getCountry()
            + " as its Country!")
        .build();
  }

}
