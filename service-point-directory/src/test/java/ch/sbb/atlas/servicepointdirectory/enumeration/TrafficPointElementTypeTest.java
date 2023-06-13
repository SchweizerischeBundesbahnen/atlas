package ch.sbb.atlas.servicepointdirectory.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TrafficPointElementTypeTest {

  @Test
  void valueShouldBeUnique() {
    Set<Integer> usedValues = new HashSet<>();
    for (TrafficPointElementType type : TrafficPointElementType.values()) {
      if (!usedValues.add(type.getValue())) {
        fail();
      }
    }
  }

  @Test
  void shouldMapFromValue() {
    assertThat(TrafficPointElementType.fromValue(0)).isEqualTo(TrafficPointElementType.BOARDING_PLATFORM);
    assertThat(TrafficPointElementType.fromValue(1)).isEqualTo(TrafficPointElementType.BOARDING_AREA);
  }
}