package ch.sbb.atlas.enumeration;


import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InfoOpportunityAttributeTypeTest {

    @Test
    public void testOfWithValidValue() {
        assertEquals(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION, InfoOpportunityAttributeType.of(15));
    }

    @Test
    public void testOfWithInvalidValue() {
        assertThrows(NoSuchElementException.class, () -> {
            InfoOpportunityAttributeType.of(99);
        });
    }

    @Test
    public void testFromWithValidValue() {
        assertEquals(InfoOpportunityAttributeType.ACOUSTIC_INFORMATION, InfoOpportunityAttributeType.from(18));
    }

    @Test
    public void testFromWithInvalidValue() {
        assertNull(InfoOpportunityAttributeType.from(99));
    }

    @Test
    public void testFromCodeWithValidString() {
        Set<InfoOpportunityAttributeType> expected = Set.of(InfoOpportunityAttributeType.STATIC_VISUAL_INFORMATION, InfoOpportunityAttributeType.ACOUSTIC_INFORMATION);
        Set<InfoOpportunityAttributeType> result = InfoOpportunityAttributeType.fromCode("15~18");
        assertEquals(expected, result);
    }

    @Test
    public void testFromCodeWithInvalidString() {
        assertTrue(InfoOpportunityAttributeType.fromCode("99~100").isEmpty());
    }

    @Test
    public void testFromCodeWithEmptyString() {
        assertTrue(InfoOpportunityAttributeType.fromCode("").isEmpty());
    }
}
