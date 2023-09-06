package ch.sbb.business.organisation.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.exception.ExportException;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionExportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

public class BusinessOrganisationVersionExportServiceTest {

  @Mock
  private BusinessOrganisationVersionExportRepository repository;

  @Mock
  private FileService fileService;

  @Mock
  private AmazonService amazonService;

  private BusinessOrganisationVersionExportService exportService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    exportService = new BusinessOrganisationVersionExportService(fileService, amazonService, repository);
  }

  @Test
  public void shouldGetFullVersionsCsv() throws IOException {
    //given
    BusinessOrganisationExportVersionWithTuInfo version1 =
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .sboid("ch:1:sboid:100001")
            .build();
    BusinessOrganisationExportVersionWithTuInfo version2 =
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .sboid("ch:1:sboid:100000")
            .build();
    List<BusinessOrganisationExportVersionWithTuInfo> versions = List.of(version1, version2);
    when(repository.findAll()).thenReturn(versions);
    //when
    File result = exportService.getFullVersionsCsv();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("full_");
    Files.delete(result.toPath());
  }

  @Test
  public void shouldGetFullVersionsJson() throws IOException {
    //given
    BusinessOrganisationExportVersionWithTuInfo version1 =
            BusinessOrganisationExportVersionWithTuInfo.builder()
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2021, 12, 31))
                    .sboid("ch:1:sboid:100001")
                    .build();
    BusinessOrganisationExportVersionWithTuInfo version2 =
            BusinessOrganisationExportVersionWithTuInfo.builder()
                    .validFrom(LocalDate.of(2022, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .sboid("ch:1:sboid:100000")
                    .build();
    List<BusinessOrganisationExportVersionWithTuInfo> versions = List.of(version1, version2);
    when(repository.findAll()).thenReturn(versions);
    //when
    File result = exportService.getFullVersionsJson();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("full_");
    Files.delete(result.toPath());
  }

  @Test
  public void shouldGetActualVersionsCsv() throws IOException {
    //given
    BusinessOrganisationExportVersionWithTuInfo version1 =
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .sboid("ch:1:sboid:100001")
            .build();
    BusinessOrganisationExportVersionWithTuInfo version2 =
        BusinessOrganisationExportVersionWithTuInfo.builder().validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .sboid("ch:1:sboid:100000")
            .build();
    List<BusinessOrganisationExportVersionWithTuInfo> versions = List.of(version1, version2);
    when(repository.findVersionsValidOn(LocalDate.now())).thenReturn(versions);
    //when
    File result = exportService.getActualVersionsCsv();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("actual_date_");
    Files.delete(result.toPath());
  }

  @Test
  public void shouldGetActualVersionsJson() throws IOException {
    //given
    BusinessOrganisationExportVersionWithTuInfo version1 =
            BusinessOrganisationExportVersionWithTuInfo.builder()
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2021, 12, 31))
                    .sboid("ch:1:sboid:100001")
                    .build();
    BusinessOrganisationExportVersionWithTuInfo version2 =
            BusinessOrganisationExportVersionWithTuInfo.builder().validFrom(LocalDate.of(2022, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .sboid("ch:1:sboid:100000")
                    .build();
    List<BusinessOrganisationExportVersionWithTuInfo> versions = List.of(version1, version2);
    when(repository.findVersionsValidOn(LocalDate.now())).thenReturn(versions);
    //when
    File result = exportService.getActualVersionsJson();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("actual_date_");
    Files.delete(result.toPath());
  }

  @Test
  public void shouldGetFutureTimetableVersionsCsv() throws IOException {
    //given
    BusinessOrganisationExportVersionWithTuInfo version1 =
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .validFrom(LocalDate.of(2021, 1, 1))
            .validTo(LocalDate.of(2021, 12, 31))
            .sboid("ch:1:sboid:100001")
            .build();
    BusinessOrganisationExportVersionWithTuInfo version2 =
        BusinessOrganisationExportVersionWithTuInfo.builder()
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .sboid("ch:1:sboid:100000")
            .build();
    List<BusinessOrganisationExportVersionWithTuInfo> versions = List.of(version1, version2);
    when(repository.findVersionsValidOn(LocalDate.now())).thenReturn(versions);
    //when
    File result = exportService.getFutureTimetableVersionsCsv();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("future_timetable_");
    Files.delete(result.toPath());
  }

  @Test
  public void shouldGetFutureTimetableVersionsJson() throws IOException {
    //given
    BusinessOrganisationExportVersionWithTuInfo version1 =
            BusinessOrganisationExportVersionWithTuInfo.builder()
                    .validFrom(LocalDate.of(2021, 1, 1))
                    .validTo(LocalDate.of(2021, 12, 31))
                    .sboid("ch:1:sboid:100001")
                    .build();
    BusinessOrganisationExportVersionWithTuInfo version2 =
            BusinessOrganisationExportVersionWithTuInfo.builder()
                    .validFrom(LocalDate.of(2022, 1, 1))
                    .validTo(LocalDate.of(2022, 12, 31))
                    .sboid("ch:1:sboid:100000")
                    .build();
    List<BusinessOrganisationExportVersionWithTuInfo> versions = List.of(version1, version2);
    when(repository.findVersionsValidOn(LocalDate.now())).thenReturn(versions);
    //when
    File result = exportService.getFutureTimetableVersionsJson();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("future_timetable_");
    Files.delete(result.toPath());
  }

  @Test
  void shouldThrowExportExceptionWhenDirDoesNotExists() {
    //given
    when(fileService.getDir()).thenReturn("./test/export");
    when(repository.findVersionsValidOn(LocalDate.now())).thenReturn(
        Collections.emptyList());
    //when

    assertThatExceptionOfType(ExportException.class).isThrownBy(
        () -> exportService.getFutureTimetableVersionsCsv());
  }

}