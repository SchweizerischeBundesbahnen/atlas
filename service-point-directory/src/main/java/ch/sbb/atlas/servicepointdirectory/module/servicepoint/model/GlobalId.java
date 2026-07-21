package ch.sbb.atlas.servicepointdirectory.module.servicepoint.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import java.util.Set;

/**
 * Value object for an official foreign Global-ID that links a service point to a national reference
 * system (e.g. Germany {@code de:05770:1282}, Austria {@code at:42:9379}).
 *
 * <p>A {@code GlobalId} instance is always <em>present and valid</em>: the {@linkplain #GlobalId(String)
 * canonical constructor} rejects any value that is blank, whitespace-padded or longer than
 * {@value AtlasFieldLengths#LENGTH_128} characters. Optionality ("no Global-ID") is therefore a
 * boundary concern handled by the caller, not modelled here.
 *
 * <p>The country of the stop the id is assigned to is a validation <em>input</em>, not part of the
 * id's identity, so it is not retained. Construct through {@link #of(String, Country)} to additionally
 * enforce the country-specific rules (MVP scope): only Germany (Didok country code 11, 80) and
 * Austria (12, 81) may carry a Global-ID, requiring the {@code de:} respectively {@code at:} prefix.
 *
 * <p>Uniqueness across stops is <em>not</em> an intrinsic rule (it cannot be decided from the value
 * alone) and is therefore enforced separately by the persistence layer, not here.
 */
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
