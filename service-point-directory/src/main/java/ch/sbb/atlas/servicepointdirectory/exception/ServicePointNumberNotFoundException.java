package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;

public class ServicePointNumberNotFoundException extends NotFoundException {

  public ServicePointNumberNotFoundException(ServicePointNumber value) {
    super("servicePointNumber", value.asString());
  }
}


