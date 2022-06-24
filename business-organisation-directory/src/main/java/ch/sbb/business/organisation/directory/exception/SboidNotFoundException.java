package ch.sbb.business.organisation.directory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion.Fields;

public class SboidNotFoundException extends NotFoundException {
  public SboidNotFoundException(String value) {
    super(Fields.sboid, value);
  }
}
