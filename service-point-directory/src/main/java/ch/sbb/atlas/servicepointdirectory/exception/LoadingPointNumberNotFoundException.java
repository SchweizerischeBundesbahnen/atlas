package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.base.service.model.exception.NotFoundException;

public class LoadingPointNumberNotFoundException extends NotFoundException {

  public LoadingPointNumberNotFoundException(Integer value) {
    super("loadingPointNumber", String.valueOf(value));
  }
}


