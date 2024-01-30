package ch.sbb.atlas.api.prm.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReferencePointAttributeTypeTest {

    @Test
    void shouldGetTypeFromValue() {
        assertThat(ReferencePointAttributeType.of(0)).isEqualTo(ReferencePointAttributeType.MAIN_STATION_ENTRANCE);
        assertThat(ReferencePointAttributeType.of(1)).isEqualTo(ReferencePointAttributeType.ALTERNATIVE_STATION_ENTRANCE);
        assertThat(ReferencePointAttributeType.of(2)).isEqualTo(ReferencePointAttributeType.ASSISTANCE_POINT);
        assertThat(ReferencePointAttributeType.of(3)).isEqualTo(ReferencePointAttributeType.INFORMATION_DESK);
        assertThat(ReferencePointAttributeType.of(4)).isEqualTo(ReferencePointAttributeType.PLATFORM);
        assertThat(ReferencePointAttributeType.of(5)).isEqualTo(ReferencePointAttributeType.NO_REFERENCE_POINT);
        assertThat(ReferencePointAttributeType.of(null)).isNull();
    }

}
