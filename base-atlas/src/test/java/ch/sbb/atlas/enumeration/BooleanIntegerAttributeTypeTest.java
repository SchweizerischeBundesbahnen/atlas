package ch.sbb.atlas.enumeration;

import ch.sbb.atlas.api.prm.enumeration.BooleanIntegerAttributeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanIntegerAttributeTypeTest {
    @Test
    public void testTrueConversion() {
        assertEquals(Boolean.TRUE, BooleanIntegerAttributeType.of(1));
    }

    @Test
    public void testFalseConversion() {
        assertEquals(Boolean.FALSE, BooleanIntegerAttributeType.of(0));
    }

    @Test
    public void testNullValue() {
        assertNull(BooleanIntegerAttributeType.of(null));
    }
}
