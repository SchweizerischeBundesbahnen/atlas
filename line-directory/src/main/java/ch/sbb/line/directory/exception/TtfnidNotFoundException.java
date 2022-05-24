package ch.sbb.line.directory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;

public class TtfnidNotFoundException extends NotFoundException {

  public TtfnidNotFoundException(String value) {
    super(TimetableFieldNumberVersion.Fields.ttfnid, value);
  }
}
