package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.CountryAndBusinessOrganisationAssociated;
import ch.sbb.atlas.servicepoint.Country;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Data
public class BusinessObjectWithCountry implements CountryAndBusinessOrganisationAssociated {

  private String anotherValue;
  private Country country;
  private String businessOrganisation;
  private LocalDate validFrom;
  private LocalDate validTo;

  public static BusinessObjectWithCountryBuilder createDummy() {
    return BusinessObjectWithCountry.builder()
        .anotherValue("value")
        .country(Country.SWITZERLAND)
        .businessOrganisation("sboid")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31));
  }

  public static BusinessObjectWithCountryBuilder createDummy1() {
    return BusinessObjectWithCountry.builder()
            .anotherValue("value1")
            .businessOrganisation("sboid1")
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31));
  }



}
