package ch.sbb.atlas.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SwissCantonTest {

    @Test
    void shouldGetCantonByNumber() {
        assertEquals(SwissCanton.ZURICH, SwissCanton.fromCantonNumber(1));
        assertEquals(SwissCanton.BERN, SwissCanton.fromCantonNumber(2));
        assertEquals(SwissCanton.LUCERNE, SwissCanton.fromCantonNumber(3));
        assertEquals(SwissCanton.URI, SwissCanton.fromCantonNumber(4));
        assertEquals(SwissCanton.SCHWYZ, SwissCanton.fromCantonNumber(5));
        assertEquals(SwissCanton.OBWALDEN, SwissCanton.fromCantonNumber(6));
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

     @Test
     void shouldGetCantonByName() {
         assertEquals(SwissCanton.ZURICH, SwissCanton.fromCantonName("Zürich"));
         assertEquals(SwissCanton.BERN, SwissCanton.fromCantonName("Bern"));
         assertEquals(SwissCanton.LUCERNE, SwissCanton.fromCantonName("Luzern"));
         assertEquals(SwissCanton.URI, SwissCanton.fromCantonName("Uri"));
         assertEquals(SwissCanton.SCHWYZ, SwissCanton.fromCantonName("Schwyz"));
         assertEquals(SwissCanton.OBWALDEN, SwissCanton.fromCantonName("Obwalden"));
         assertEquals(SwissCanton.NIDWALDEN, SwissCanton.fromCantonName("Nidwalden"));
         assertEquals(SwissCanton.GLARUS, SwissCanton.fromCantonName("Glarus"));
         assertEquals(SwissCanton.ZUG, SwissCanton.fromCantonName("Zug"));
         assertEquals(SwissCanton.FRIBOURG, SwissCanton.fromCantonName("Fribourg"));
         assertEquals(SwissCanton.SOLOTHURN, SwissCanton.fromCantonName("Solothurn"));
         assertEquals(SwissCanton.BASEL_CITY, SwissCanton.fromCantonName("Basel-Stadt"));
         assertEquals(SwissCanton.BASEL_COUNTRY, SwissCanton.fromCantonName("Basel-Landschaft"));
         assertEquals(SwissCanton.THURGAU, SwissCanton.fromCantonName("Thurgau"));
         assertEquals(SwissCanton.TICINO, SwissCanton.fromCantonName("Ticino"));
         assertEquals(SwissCanton.VAUD, SwissCanton.fromCantonName("Vaud"));
         assertEquals(SwissCanton.VALAIS, SwissCanton.fromCantonName("Valais"));
         assertEquals(SwissCanton.NEUCHATEL, SwissCanton.fromCantonName("Neuchâtel"));
         assertEquals(SwissCanton.GENEVE, SwissCanton.fromCantonName("Genève"));
         assertEquals(SwissCanton.JURA, SwissCanton.fromCantonName("Jura"));
     }

}
