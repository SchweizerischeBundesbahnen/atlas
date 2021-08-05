package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.Version;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration-test")
public class VersionRepositoryTest {

    private final VersionRepository versionRepository;

    @Autowired
    public VersionRepositoryTest(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    @Test
    void shouldGetVersion() {
        //given
        Version version = Version.builder().build();
        versionRepository.save(version);

        //when
        Version result = versionRepository.findById(1000L).orElseThrow();

        //then
        assertThat(result.getId()).isEqualTo(1000L);

    }

}