package ch.sbb.atlas.servicepointdirectory.enumeration;

import static org.junit.jupiter.api.Assertions.fail;

import ch.sbb.atlas.servicepoint.Country;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class CountryTest {

  @Test
  void isoCodeUicCodeCombinationShouldBeUnique() {
    Set<Pair<String, Integer>> usedValues = new HashSet<>();
    for (Country country : Country.values()) {
      if (!usedValues.add(Pair.of(country.getIsoCode(), country.getUicCode()))) {
        fail();
      }
    }
  }

}