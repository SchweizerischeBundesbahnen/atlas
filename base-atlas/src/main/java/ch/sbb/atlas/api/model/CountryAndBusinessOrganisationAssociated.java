package ch.sbb.atlas.api.model;

import ch.sbb.atlas.servicepoint.Country;

public interface CountryAndBusinessOrganisationAssociated extends BusinessOrganisationAssociated {

  Country getCountry();

}
