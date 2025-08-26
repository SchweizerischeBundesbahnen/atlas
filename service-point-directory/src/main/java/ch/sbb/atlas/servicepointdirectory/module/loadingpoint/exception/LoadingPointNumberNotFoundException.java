package ch.sbb.atlas.servicepointdirectory.module.loadingpoint.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;

public class LoadingPointNumberNotFoundException extends NotFoundException {

  public LoadingPointNumberNotFoundException(ServicePointNumber servicePointNumber, Integer value) {
    super("loadingPointNumber", servicePointNumber.asString() + ":" + value);
  }
}


