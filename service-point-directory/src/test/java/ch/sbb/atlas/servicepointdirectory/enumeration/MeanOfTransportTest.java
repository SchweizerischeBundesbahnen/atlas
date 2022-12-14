package ch.sbb.atlas.servicepointdirectory.enumeration;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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