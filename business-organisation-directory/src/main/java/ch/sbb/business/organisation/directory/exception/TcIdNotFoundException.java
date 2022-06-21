package ch.sbb.business.organisation.directory.exception;

import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.business.organisation.directory.entity.BoTcLink.Fields;

public class TcIdNotFoundException extends NotFoundException {

  public TcIdNotFoundException(Integer value){
    super(Fields.transportCompanyId, String.valueOf(value));
  }

}
