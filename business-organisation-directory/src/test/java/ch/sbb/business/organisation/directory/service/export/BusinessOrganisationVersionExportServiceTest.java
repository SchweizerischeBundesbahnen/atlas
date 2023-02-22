package ch.sbb.business.organisation.directory.service.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.exception.ExportException;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BusinessOrganisationVersionExportServiceTest {

    @Mock
    private BusinessOrganisationVersionRepository repository;

    @Mock
    private FileService fileService;

    @Mock
    private AmazonService amazonService;

    private BusinessOrganisationVersionExportService exportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        exportService = new BusinessOrganisationVersionExportService(fileService, amazonService,
            repository);
    }

    @Test
    public void shouldGetFullVersionsCsv() {
        //given
        BusinessOrganisationVersion version1 =
            BusinessOrganisationVersion.builder()
                .validFrom(LocalDate.of(2021, 1, 1))
                .validTo(LocalDate.of(2021, 12, 31))
                .sboid("ch:1:sboid:100001")
                .build();
        BusinessOrganisationVersion version2 =
            BusinessOrganisationVersion.builder()
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .sboid("ch:1:sboid:100000")
                .build();
        List<BusinessOrganisationVersion> versions = List.of(version1, version2);
        when(repository.getFullLineVersions()).thenReturn(versions);
        //when
        File result = exportService.getFullVersionsCsv();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getName()).contains("full_");
        result.delete();
    }

    @Test
    public void shouldGetActualVersionsCsv() {
        //given
        BusinessOrganisationVersion version1 =
            BusinessOrganisationVersion.builder()
                .validFrom(LocalDate.of(2021, 1, 1))
                .validTo(LocalDate.of(2021, 12, 31))
                .sboid("ch:1:sboid:100001")
                .build();
        BusinessOrganisationVersion version2 =
            BusinessOrganisationVersion.builder().validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .sboid("ch:1:sboid:100000")
                .build();
        List<BusinessOrganisationVersion> versions = List.of(version1, version2);
        when(repository.getActualLineVersions(LocalDate.now())).thenReturn(versions);
        //when
        File result = exportService.getActualVersionsCsv();
        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getName()).contains("actual_date_");
        result.delete();
    }

    @Test
    public void shouldGetFutureTimetableVersionsCsv() {
        //given
        BusinessOrganisationVersion version1 =
            BusinessOrganisationVersion.builder()
                .validFrom(LocalDate.of(2021, 1, 1))
                .validTo(LocalDate.of(2021, 12, 31))
                .sboid("ch:1:sboid:100001")
                .build();
        BusinessOrganisationVersion version2 =
            BusinessOrganisationVersion.builder()
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .sboid("ch:1:sboid:100000")
                .build();
        List<BusinessOrganisationVersion> versions = List.of(version1, version2);
        when(repository.getActualLineVersions(LocalDate.now())).thenReturn(versions);
        //when
        File result = exportService.getFutureTimetableVersionsCsv();
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
        when(repository.getActualLineVersions(LocalDate.now())).thenReturn(
            Collections.emptyList());
        //when

        assertThatExceptionOfType(ExportException.class).isThrownBy(
            () -> exportService.getFutureTimetableVersionsCsv());
    }

}