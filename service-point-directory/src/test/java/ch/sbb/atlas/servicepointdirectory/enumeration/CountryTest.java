package ch.sbb.atlas.servicepointdirectory.enumeration;

import ch.sbb.atlas.servicepoint.Country;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static ch.sbb.atlas.servicepoint.Country.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

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

  @Test
  void shouldGetSloidCompatibleCountryCodes(){
    //then
    assertThat(Country.SLOID_COMPATIBLE_COUNTRIES.size()).isEqualTo(4);
    assertThat(Country.SLOID_COMPATIBLE_COUNTRIES).containsExactlyInAnyOrder(GERMANY_BUS, AUSTRIA_BUS, ITALY_BUS, FRANCE_BUS);
  }

}