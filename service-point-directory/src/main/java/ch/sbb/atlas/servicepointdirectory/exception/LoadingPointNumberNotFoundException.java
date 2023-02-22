package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;

public class LoadingPointNumberNotFoundException extends NotFoundException {

  public LoadingPointNumberNotFoundException(ServicePointNumber servicePointNumber, Integer value) {
    super("loadingPointNumber", servicePointNumber.asString() + ":" + value);
  }
}


