package ch.sbb.line.directory.service.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.exception.ExportException;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SublineVersionExportServiceTest {

    @Mock
    private SublineVersionRepository sublineVersionRepository;

    @Mock
    private FileService fileService;

    @Mock
    private AmazonService amazonService;

    private SublineVersionExportService sublineVersionExportService;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        sublineVersionExportService = new SublineVersionExportService(fileService, amazonService,
            sublineVersionRepository);
    }

    @Test
     void shouldGetFullVersionsCsv() {
        //given
        SublineVersion lineVersion1 = SublineTestData.sublineVersionBuilder().build();
        SublineVersion lineVersion2 = SublineTestData.sublineVersionBuilder()
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .description("desc2")
            .build();
        List<SublineVersion> sublineVersions = List.of(lineVersion1, lineVersion2);
        when(sublineVersionRepository.getFullSublineVersions()).thenReturn(sublineVersions);
        //when
        File result = sublineVersionExportService.getFullVersionsCsv();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getName()).contains("full_");
        result.delete();
    }

    @Test
     void shouldGetActualVersionsCsv() {
        //given
        SublineVersion lineVersion1 = SublineTestData.sublineVersionBuilder().build();
        SublineVersion lineVersion2 = SublineTestData.sublineVersionBuilder()
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .description("desc2")
            .build();
        List<SublineVersion> sublineVersions = List.of(lineVersion1, lineVersion2);
        when(sublineVersionRepository.getFullSublineVersions()).thenReturn(sublineVersions);
        //when
        File result = sublineVersionExportService.getActualVersionsCsv();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getName()).contains("actual_date_");
        result.delete();
    }

    @Test
     void shouldGetFutureTimetableVersionsCsv() {
        //given
        SublineVersion lineVersion1 = SublineTestData.sublineVersionBuilder().build();
        SublineVersion lineVersion2 = SublineTestData.sublineVersionBuilder()
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .description("desc2")
            .build();
        List<SublineVersion> sublineVersions = List.of(lineVersion1, lineVersion2);
        when(sublineVersionRepository.getFullSublineVersions()).thenReturn(sublineVersions);
        //when
        File result = sublineVersionExportService.getFutureTimetableVersionsCsv();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getName()).contains("future_timetable_");
        result.delete();
    }

    @Test
    void shouldThrowExportExceptionWhenDirDoesNotExists() {
        //given
        when(fileService.getDir()).thenReturn("./test/export");
        when(sublineVersionRepository.getActualSublineVersions(LocalDate.now())).thenReturn(
            Collections.emptyList());
        //when

        assertThatExceptionOfType(ExportException.class).isThrownBy(
            () -> sublineVersionExportService.getFutureTimetableVersionsCsv());
    }

}