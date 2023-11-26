package ch.sbb.atlas.servicepointdirectory.enumeration;

import static ch.sbb.atlas.servicepoint.Country.AUSTRIA_BUS;
import static ch.sbb.atlas.servicepoint.Country.FRANCE_BUS;
import static ch.sbb.atlas.servicepoint.Country.GERMANY_BUS;
import static ch.sbb.atlas.servicepoint.Country.ITALY_BUS;
import static org.assertj.core.api.Assertions.assertThat;
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

  @Test
  void shouldGetSloidCompatibleCountryCodes() {
    //then
    assertThat(Country.SLOID_COMPATIBLE_COUNTRIES).hasSize(4)
        .containsExactlyInAnyOrder(GERMANY_BUS, AUSTRIA_BUS, ITALY_BUS, FRANCE_BUS);
  }

}