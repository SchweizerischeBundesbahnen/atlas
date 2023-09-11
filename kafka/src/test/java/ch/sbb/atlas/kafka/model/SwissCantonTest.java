package ch.sbb.atlas.kafka.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwissCantonTest {

    @Test
    void shouldGetCantonByNumber() {
        assertEquals(SwissCanton.ZURICH, SwissCanton.fromCantonNumber(1));
        assertEquals(SwissCanton.BERN, SwissCanton.fromCantonNumber(2));
        assertEquals(SwissCanton.LUCERNE, SwissCanton.fromCantonNumber(3));
        assertEquals(SwissCanton.URI, SwissCanton.fromCantonNumber(4));
        assertEquals(SwissCanton.SCHWYZ, SwissCanton.fromCantonNumber(5));
        assertEquals(SwissCanton.OBWALDEN, SwissCanton.fromCantonNumber(6));
        assertEquals(SwissCanton.NIDWALDEN, SwissCanton.fromCantonNumber(7));
        assertEquals(SwissCanton.GLARUS, SwissCanton.fromCantonNumber(8));
        assertEquals(SwissCanton.ZUG, SwissCanton.fromCantonNumber(9));
        assertEquals(SwissCanton.FRIBOURG, SwissCanton.fromCantonNumber(10));
        assertEquals(SwissCanton.SOLOTHURN, SwissCanton.fromCantonNumber(11));
        assertEquals(SwissCanton.BASEL_CITY, SwissCanton.fromCantonNumber(12));
        assertEquals(SwissCanton.BASEL_COUNTRY, SwissCanton.fromCantonNumber(13));
        assertEquals(SwissCanton.SCHAFFHAUSEN, SwissCanton.fromCantonNumber(14));
        assertEquals(SwissCanton.APPENZELL_AUSSERRHODEN, SwissCanton.fromCantonNumber(15));
        assertEquals(SwissCanton.APPENZELL_INNERRHODEN, SwissCanton.fromCantonNumber(16));
        assertEquals(SwissCanton.ST_GALLEN, SwissCanton.fromCantonNumber(17));
        assertEquals(SwissCanton.GRAUBUNDEN, SwissCanton.fromCantonNumber(18));
        assertEquals(SwissCanton.AARGAU, SwissCanton.fromCantonNumber(19));
        assertEquals(SwissCanton.THURGAU, SwissCanton.fromCantonNumber(20));
        assertEquals(SwissCanton.TICINO, SwissCanton.fromCantonNumber(21));
        assertEquals(SwissCanton.VAUD, SwissCanton.fromCantonNumber(22));
        assertEquals(SwissCanton.VALAIS, SwissCanton.fromCantonNumber(23));
        assertEquals(SwissCanton.NEUCHATEL, SwissCanton.fromCantonNumber(24));
        assertEquals(SwissCanton.GENEVE, SwissCanton.fromCantonNumber(25));
        assertEquals(SwissCanton.JURA, SwissCanton.fromCantonNumber(26));
    }

}
