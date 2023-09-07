package ch.sbb.atlas.servicepoint.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StopPointTypeTest {

    @Test
    void shouldTestStopPointType() {
        assertEquals(StopPointType.ORDERLY, StopPointType.from(10));
        assertEquals(StopPointType.ON_REQUEST, StopPointType.from(20));
        assertEquals(StopPointType.ZONE_ON_REQUEST, StopPointType.from(30));
        assertEquals(StopPointType.TEMPORARY, StopPointType.from(40));
        assertEquals(StopPointType.OUT_OF_ORDER, StopPointType.from(50));
        assertEquals(StopPointType.UNKNOWN, StopPointType.from(0));
    }

}
