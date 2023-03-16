package ch.sbb.atlas.api.model;

import java.time.LocalDate;

public interface BusinessOrganisationAssociated {

  String getBusinessOrganisation();

  LocalDate getValidFrom();

  LocalDate getValidTo();

}
