package ch.sbb.atlas.servicepointdirectory.enumeration;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MeansOfTransportTest {

    @Test
    void rankShouldBeUnique() {
        Set<Integer> usedRanks = new HashSet<>();
        for (MeansOfTransport mean : MeansOfTransport.values()) {
            if (!usedRanks.add(mean.getRank())) {
                fail();
            }
        }
    }
}