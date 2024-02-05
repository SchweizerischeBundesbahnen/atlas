package ch.sbb.atlas.enumeration;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ContactPointTypeTest {
    @Test
    public void testEnumValues() {
        assertTrue(EnumSet.of(ContactPointType.INFORMATION_DESK, ContactPointType.TICKET_COUNTER)
                        .equals(EnumSet.allOf(ContactPointType.class)),
                "ContactPointType enthält nicht die erwarteten Werte");
    }
}
