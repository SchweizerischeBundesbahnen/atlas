package ch.sbb.atlas.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InfoOpportunityAttributeTypeTest {

    @Test
    void testOfWithValidValue() {
        assertEquals(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION, InfoOpportunityAttributeType.of(15));
    }

    @Test
    void testOfWithInvalidValue() {
        assertThrows(NoSuchElementException.class, () -> {
            InfoOpportunityAttributeType.of(99);
        });
    }

    @Test
    void testFromWithValidValue() {
        assertEquals(InfoOpportunityAttributeType.ACOUSTIC_INFORMATION, InfoOpportunityAttributeType.from(18));
    }

    @Test
    void testFromWithInvalidValue() {
        assertNull(InfoOpportunityAttributeType.from(99));
    }

    @Test
    void testFromCodeWithValidString() {
        Set<InfoOpportunityAttributeType> expected = Set.of(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION, InfoOpportunityAttributeType.ACOUSTIC_INFORMATION);
        Set<InfoOpportunityAttributeType> result = InfoOpportunityAttributeType.fromCode("15~18");
        assertEquals(expected, result);
    }

    @Test
    void testFromCodeWithInvalidString() {
        assertTrue(InfoOpportunityAttributeType.fromCode("99~100").isEmpty());
    }

    @Test
    void testFromCodeWithEmptyString() {
        assertTrue(InfoOpportunityAttributeType.fromCode("").isEmpty());
    }
}
