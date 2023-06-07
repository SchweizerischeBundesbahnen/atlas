package ch.sbb.atlas.business.organisation.exception;

import ch.sbb.atlas.model.exception.NotFoundException;

public class SboidNotFoundException extends NotFoundException {

  public SboidNotFoundException(String value) {
    super("sboid", value);
  }
}
