package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;

public class ServicePointNumberNotFoundException extends NotFoundException {

  private final String message;

  public ServicePointNumberNotFoundException(ServicePointNumber value) {
    super("servicePointNumber", value.asString());
    this.message = "ServicePointNumber with value: " + value.asString() + " not found";
  }

  @Override
  public String getMessage() {
    return this.message;
  }

}
