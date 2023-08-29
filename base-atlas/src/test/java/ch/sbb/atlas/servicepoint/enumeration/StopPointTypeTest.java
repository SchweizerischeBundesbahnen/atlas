package ch.sbb.atlas.servicepoint.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StopPointTypeTest {

    @Test
    void shouldGetOrderlyStopPointType() {
        assertEquals(StopPointType.ORDERLY, StopPointType.from(StopPointType.ORDERLY.getId()));
    }

    @Test
    void shouldGetOnRequestStopPointType() {
        assertEquals(StopPointType.ON_REQUEST, StopPointType.from(StopPointType.ON_REQUEST.getId()));
    }

    @Test
    void shouldGetZoneOnRequestStopPointType() {
        assertEquals(StopPointType.ZONE_ON_REQUEST, StopPointType.from(StopPointType.ZONE_ON_REQUEST.getId()));
    }

    @Test
    void shouldGetTemporaryStopPointType() {
        assertEquals(StopPointType.TEMPORARY, StopPointType.from(StopPointType.TEMPORARY.getId()));
    }

    @Test
    void shouldGetOutOfOrderStopPointType() {
        assertEquals(StopPointType.OUT_OF_ORDER, StopPointType.from(StopPointType.OUT_OF_ORDER.getId()));
    }

    @Test
    void shouldGetUnknownStopPointType() {
        assertEquals(StopPointType.UNKNOWN, StopPointType.from(StopPointType.UNKNOWN.getId()));
    }

}
