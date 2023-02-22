package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;

public class SloidNotFoundException extends NotFoundException {

  public SloidNotFoundException(String sloid) {
    super("sloid", sloid);
  }
}


