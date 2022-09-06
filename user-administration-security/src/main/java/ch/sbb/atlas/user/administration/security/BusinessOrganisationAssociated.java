package ch.sbb.atlas.user.administration.security;

import java.time.LocalDate;

public interface BusinessOrganisationAssociated {

  String getBusinessOrganisation();

  LocalDate getValidFrom();

  LocalDate getValidTo();

}
