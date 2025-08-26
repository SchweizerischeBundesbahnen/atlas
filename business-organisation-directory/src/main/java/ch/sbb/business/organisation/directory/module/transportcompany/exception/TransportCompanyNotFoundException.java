package ch.sbb.business.organisation.directory.module.transportcompany.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.business.organisation.directory.module.transportcompany.entity.TransportCompany.Fields;

public class TransportCompanyNotFoundException extends NotFoundException {

  public TransportCompanyNotFoundException(Long value) {
    super(Fields.id, String.valueOf(value));
  }

}
