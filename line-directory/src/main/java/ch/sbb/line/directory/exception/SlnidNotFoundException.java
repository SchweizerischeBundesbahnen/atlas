package ch.sbb.line.directory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.line.directory.entity.LineVersion.Fields;

public class SlnidNotFoundException extends NotFoundException {

  public SlnidNotFoundException(String value) {
    super(Fields.slnid, value);
  }
}


