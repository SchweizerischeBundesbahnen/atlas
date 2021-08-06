package ch.sbb.timetable.field.number.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.model.VersionModel;
import ch.sbb.timetable.field.number.repository.VersionRepository;

@IntegrationTest
public class VersionControllerTest {

    private final VersionController versionController;
    private final VersionRepository versionRepository;

    @Autowired
    public VersionControllerTest(VersionController versionController, VersionRepository versionRepository) {
        this.versionController = versionController;
        this.versionRepository = versionRepository;
    }

    @Test
    void shouldCreateVersion() {
        // Given
        String ttfnid = "ch:1:fpfnid:100000";
        VersionModel versionModel = VersionModel.builder().ttfnid(ttfnid).build();

        // When
        VersionModel result = versionController.createVersion(versionModel);

        // Then
        assertThat(result.getTtfnid()).isEqualTo(ttfnid);
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void shouldGetVersion() {
        // Given
        Version version = Version.builder().ttfnid("ch:1:fpfnid:100000")
                                           .name("FPFN Name")
                                           .number("BEX")
                                           .swissTimetableFieldNumber("b0.BEX")
                                           .validFrom(LocalDate.of(2020, 12, 12))
                                           .validTo(LocalDate.of(2099, 12, 12))
                                           .build();
        version = versionRepository.save(version);

        // When
        VersionModel result = versionController.getVersion(version.getId());

        // Then
        assertThat(result).usingRecursiveComparison().isEqualTo(version);
    }

    @Test
    void shouldDeleteVersion() {
        // Given
        Version version = Version.builder().ttfnid("ch:1:fpfnid:100000")
                                           .name("FPFN Name")
                                           .number("BEX")
                                           .swissTimetableFieldNumber("b0.BEX")
                                           .validFrom(LocalDate.of(2020, 12, 12))
                                           .validTo(LocalDate.of(2099, 12, 12))
                                           .build();
        version = versionRepository.save(version);

        // When
        versionController.deleteVersion(version.getId());

        // Then
        assertThat(versionRepository.existsById(version.getId())).isFalse();
    }

    @Test
    void shouldReturnNotFoundOnVersionDelete() {
        assertThatThrownBy(() -> versionController.deleteVersion(1L)).isInstanceOf(ResponseStatusException.class).extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }
}