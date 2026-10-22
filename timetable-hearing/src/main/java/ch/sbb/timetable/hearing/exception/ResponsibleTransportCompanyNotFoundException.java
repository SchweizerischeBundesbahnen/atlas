package ch.sbb.timetable.hearing.exception;

import ch.sbb.atlas.model.exception.NotFoundException;

public class ResponsibleTransportCompanyNotFoundException extends NotFoundException {

  public ResponsibleTransportCompanyNotFoundException(Long id) {
    super("responsibleTransportCompanies.id", String.valueOf(id));
  }
}
