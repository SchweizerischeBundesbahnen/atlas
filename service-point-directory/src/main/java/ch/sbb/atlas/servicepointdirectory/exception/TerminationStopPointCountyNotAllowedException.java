package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.servicepoint.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TerminationStopPointCountyNotAllowedException extends BaseException {

  private final Country country;

  @Override
  protected int getHttpStatus() {
    return HttpStatus.PRECONDITION_FAILED.value();
  }

  @Override
  protected String getCustomMessage() {
    return "A termination workflow is not allowed for the country " + country.name();
  }

  @Override
  protected String getCustomError() {
    return "Termination workflow not allowed";
  }

}
