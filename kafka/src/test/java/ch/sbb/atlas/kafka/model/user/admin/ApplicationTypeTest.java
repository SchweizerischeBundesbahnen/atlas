package ch.sbb.atlas.kafka.model.user.admin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTypeTest {

    @Test
    void shouldTestAllApplicationTypeEnums() {
        assertThat(ApplicationType.values().length).isEqualTo(6);
        assertThat(ApplicationType.TTFN.name()).isEqualTo("TTFN");
        assertThat(ApplicationType.LIDI.name()).isEqualTo("LIDI");
        assertThat(ApplicationType.BODI.name()).isEqualTo("BODI");
        assertThat(ApplicationType.TIMETABLE_HEARING.name()).isEqualTo("TIMETABLE_HEARING");
        assertThat(ApplicationType.SEPODI.name()).isEqualTo("SEPODI");
        assertThat(ApplicationType.PRM.name()).isEqualTo("PRM");
    }

}
