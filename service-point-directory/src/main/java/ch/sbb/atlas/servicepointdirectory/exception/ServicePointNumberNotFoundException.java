package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;

public class ServicePointNumberNotFoundException extends NotFoundException {

  public ServicePointNumberNotFoundException(ServicePointNumber value) {
    super("servicePointNumber", value.asString());
  }
}


