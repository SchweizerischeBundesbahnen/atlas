package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.prm.directory.entity.StopPointVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ServicePointWithNotSwissCountryNotAllowedException extends AtlasException {

  private final StopPointVersion stopPointVersion;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.PRECONDITION_FAILED.value())
        .message("PRM does not allow to create StopPoints from non-Swiss ServicePoints!")
        .error("The given Sloid [" + stopPointVersion.getSloid() + "], with " + stopPointVersion.getNumber()+ " has " + stopPointVersion.getNumber().getCountry() + " as its Country!")
        .build();
  }

}
