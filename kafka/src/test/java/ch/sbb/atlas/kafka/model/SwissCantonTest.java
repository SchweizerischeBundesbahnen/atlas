package ch.sbb.atlas.kafka.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwissCantonTest {

    @Test
    void shouldGetZurichCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.ZURICH.getNumber());
        assertEquals(SwissCanton.ZURICH, swissCanton);
    }

    @Test
    void shouldGetBernCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.BERN.getNumber());
        assertEquals(SwissCanton.BERN, swissCanton);
    }

    @Test
    void shouldGetLucerneCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.LUCERNE.getNumber());
        assertEquals(SwissCanton.LUCERNE, swissCanton);
    }

    @Test
    void shouldGetUriCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.URI.getNumber());
        assertEquals(SwissCanton.URI, swissCanton);
    }

    @Test
    void shouldGetSchwyzCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.SCHWYZ.getNumber());
        assertEquals(SwissCanton.SCHWYZ, swissCanton);
    }

    @Test
    void shouldGetObwaldenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.OBWALDEN.getNumber());
        assertEquals(SwissCanton.OBWALDEN, swissCanton);
    }

    @Test
    void shouldGetNidwaldenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.NIDWALDEN.getNumber());
        assertEquals(SwissCanton.NIDWALDEN, swissCanton);
    }

    @Test
    void shouldGetGlarusCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.GLARUS.getNumber());
        assertEquals(SwissCanton.GLARUS, swissCanton);
    }

    @Test
    void shouldGetZugCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.ZUG.getNumber());
        assertEquals(SwissCanton.ZUG, swissCanton);
    }

    @Test
    void shouldGetFribourgCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.FRIBOURG.getNumber());
        assertEquals(SwissCanton.FRIBOURG, swissCanton);
    }

    @Test
    void shouldGetSolothurnCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.SOLOTHURN.getNumber());
        assertEquals(SwissCanton.SOLOTHURN, swissCanton);
    }

    @Test
    void shouldGetBaselCityCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.BASEL_CITY.getNumber());
        assertEquals(SwissCanton.BASEL_CITY, swissCanton);
    }

    @Test
    void shouldGetBaselCountryCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.BASEL_COUNTRY.getNumber());
        assertEquals(SwissCanton.BASEL_COUNTRY, swissCanton);
    }

    @Test
    void shouldGetSchaffhausenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.SCHAFFHAUSEN.getNumber());
        assertEquals(SwissCanton.SCHAFFHAUSEN, swissCanton);
    }

    @Test
    void shouldGetAppenzellAusserrhodenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.APPENZELL_AUSSERRHODEN.getNumber());
        assertEquals(SwissCanton.APPENZELL_AUSSERRHODEN, swissCanton);
    }

    @Test
    void shouldGetAppenzeellInnerrhodenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.APPENZELL_INNERRHODEN.getNumber());
        assertEquals(SwissCanton.APPENZELL_INNERRHODEN, swissCanton);
    }

    @Test
    void shouldGetStGallenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.ST_GALLEN.getNumber());
        assertEquals(SwissCanton.ST_GALLEN, swissCanton);
    }

    @Test
    void shouldGetGraubundenCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.GRAUBUNDEN.getNumber());
        assertEquals(SwissCanton.GRAUBUNDEN, swissCanton);
    }

    @Test
    void shouldGetAargauCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.AARGAU.getNumber());
        assertEquals(SwissCanton.AARGAU, swissCanton);
    }

    @Test
    void shouldGetThurgauCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.THURGAU.getNumber());
        assertEquals(SwissCanton.THURGAU, swissCanton);
    }

    @Test
    void shouldGetTicinoCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.TICINO.getNumber());
        assertEquals(SwissCanton.TICINO, swissCanton);
    }

    @Test
    void shouldGetVaudCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.VAUD.getNumber());
        assertEquals(SwissCanton.VAUD, swissCanton);
    }

    @Test
    void shouldGetValaisCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.VALAIS.getNumber());
        assertEquals(SwissCanton.VALAIS, swissCanton);
    }

    @Test
    void shouldGetNeuchatelCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.NEUCHATEL.getNumber());
        assertEquals(SwissCanton.NEUCHATEL, swissCanton);
    }

    @Test
    void shouldGetGeneveCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.GENEVE.getNumber());
        assertEquals(SwissCanton.GENEVE, swissCanton);
    }

    @Test
    void shouldGetJuraCanton() {
        SwissCanton swissCanton = SwissCanton.fromCantonNumber(SwissCanton.JURA.getNumber());
        assertEquals(SwissCanton.JURA, swissCanton);
    }

}
