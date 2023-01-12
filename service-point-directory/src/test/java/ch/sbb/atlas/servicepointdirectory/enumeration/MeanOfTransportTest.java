package ch.sbb.atlas.servicepointdirectory.enumeration;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MeanOfTransportTest {

  @Test
  void rankShouldBeUnique() {
    Set<Integer> usedRanks = new HashSet<>();
    for (MeanOfTransport mean : MeanOfTransport.values()) {
      if (!usedRanks.add(mean.getRank())) {
        fail();
      }
    }
  }
}