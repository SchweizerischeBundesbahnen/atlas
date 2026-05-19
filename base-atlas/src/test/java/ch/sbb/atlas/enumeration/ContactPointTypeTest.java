package ch.sbb.atlas.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class ContactPointTypeTest {

    @Test
    void shouldContainAllEnumValues() {
        assertThat(EnumSet.of(ContactPointType.INFORMATION_DESK, ContactPointType.TICKET_COUNTER))
            .withFailMessage("ContactPointType enthält nicht die erwarteten Werte")
            .containsAll(EnumSet.allOf(ContactPointType.class));
    }
}
