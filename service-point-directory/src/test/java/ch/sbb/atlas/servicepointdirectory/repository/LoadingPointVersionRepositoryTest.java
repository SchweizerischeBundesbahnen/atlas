package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public class LoadingPointVersionRepositoryTest {

    private final LoadingPointRepository loadingPointRepository;

    @Autowired
    public LoadingPointVersionRepositoryTest(LoadingPointRepository loadingPointRepository){
        this.loadingPointRepository = loadingPointRepository;
    }

    @AfterEach
    void tearDown() {
        loadingPointRepository.deleteAll();
    }

    @Test
    void shouldSaveLoadingPoint() {
        // given
        LoadingPointVersion loadingPointVersion = LoadingPointVersion.builder()
                .number(1)
                .designation("Ladestelle")
                .designationLong("Grosse Ladestelle")
                .connectionPoint(true)
                .servicePointNumber(1L)
                .validFrom(LocalDate.of(2022,1,1))
                .validTo(LocalDate.of(2022,12,31))
                .build();

        // when
        LoadingPointVersion savedVersion = loadingPointRepository.save(loadingPointVersion);

        // then
        assertThat(savedVersion.getId()).isNotNull();
    }

}