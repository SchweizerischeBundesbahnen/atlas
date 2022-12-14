package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public class TrafficPointElementVersionRepositoryTest {

    private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

    @Autowired
    public TrafficPointElementVersionRepositoryTest(TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
        this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
    }

    @AfterEach
    void tearDown() {
        trafficPointElementVersionRepository.deleteAll();
    }

    @Test
    void shouldSaveTrafficPointElementVersionWithoutParent() {
        // given
        TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
                .designation("Bezeichnung")
                .designationOperational("Betriebliche Bezeichnung")
                .servicePointNumber(1)
                .sloid("ch:1:sloid:123")
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .build();

        // when
        TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(trafficPointElementVersion);

        // then
        assertThat(savedVersion.getId()).isNotNull();
    }

    @Test
    void shouldSaveTrafficPointElementVersionWithParent() {
        // given
        TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
                .designation("Bezeichnung")
                .designationOperational("Betriebliche Bezeichnung")
                .servicePointNumber(1)
                .sloid("ch:1:sloid:123")
                .parentSloid("ch:1:sloid:1")
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .build();

        // when
        TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(trafficPointElementVersion);

        // then
        assertThat(savedVersion.getId()).isNotNull();
        assertThat(savedVersion.getParentSloid()).isNotNull();
    }

}