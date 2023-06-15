package ch.sbb.atlas.api.model;

import ch.sbb.atlas.api.user.administration.enumeration.Country;

public interface CountryAndBusinessOrganisationAssociated extends BusinessOrganisationAssociated {

  Country getCountry();

}
