package ch.sbb.atlas.servicepointdirectory.module.servicepoint.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import java.util.Set;

public record GlobalId(String value) {

  private static final Set<Country> GERMANY_COUNTRIES = Set.of(Country.GERMANY, Country.GERMANY_BUS);
  private static final Set<Country> AUSTRIA_COUNTRIES = Set.of(Country.AUSTRIA, Country.AUSTRIA_BUS);
  private static final String GERMANY_PREFIX = "de:";
  private static final String AUSTRIA_PREFIX = "at:";

  public GlobalId {
    if (value == null || value.isBlank()) {
      throw InvalidGlobalIdException.illegalArguments();
    }
    if (!value.equals(value.strip())) {
      throw InvalidGlobalIdException.whitespace();
    }
    if (value.length() > AtlasFieldLengths.LENGTH_128) {
      throw InvalidGlobalIdException.maxLength(AtlasFieldLengths.LENGTH_128);
    }
  }

  public static GlobalId of(String value, Country country) {
    GlobalId globalId = new GlobalId(value);
    requireCountryPrefix(country, globalId.value());
    return globalId;
  }

  private static void requireCountryPrefix(Country country, String value) {
    if (GERMANY_COUNTRIES.contains(country)) {
      if (!value.startsWith(GERMANY_PREFIX)) {
        throw InvalidGlobalIdException.countryMismatch(GERMANY_PREFIX, value);
      }
    } else if (AUSTRIA_COUNTRIES.contains(country)) {
      if (!value.startsWith(AUSTRIA_PREFIX)) {
        throw InvalidGlobalIdException.countryMismatch(AUSTRIA_PREFIX, value);
      }
    } else {
      throw InvalidGlobalIdException.notAllowedForCountry();
    }
  }

}
