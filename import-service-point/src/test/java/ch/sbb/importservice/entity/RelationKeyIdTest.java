package ch.sbb.importservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RelationKeyIdTest {
    @Test
    public void testGetterSetter() {
        String sloid = "sloid1";
        String rpSloid = "rpSloid1";

        RelationKeyId key = new RelationKeyId(sloid, rpSloid);

        assertEquals(sloid, key.getSloid());
        assertEquals(rpSloid, key.getRpSloid());
    }

    @Test
    public void testEqualsAndHashCode() {
        RelationKeyId key1 = new RelationKeyId("sloid1", "rpSloid1");
        RelationKeyId key2 = new RelationKeyId("sloid1", "rpSloid1");
        RelationKeyId key3 = new RelationKeyId("sloid2", "rpSloid2");

        assertEquals(key1, key2);
        assertNotEquals(key1, key3);

        assertEquals(key1.hashCode(), key2.hashCode());
        assertNotEquals(key1.hashCode(), key3.hashCode());
    }
}
