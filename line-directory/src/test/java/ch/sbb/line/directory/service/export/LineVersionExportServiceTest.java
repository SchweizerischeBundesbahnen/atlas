package ch.sbb.line.directory.service.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.exception.ExportException;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineVersionExportServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private FileService fileService;

  @Mock
  private AmazonService amazonService;

  private LineVersionExportService lineVersionExportService;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    lineVersionExportService = new LineVersionExportService(fileService, amazonService,
        lineVersionRepository);
  }

  @Test
   void shouldGetFullVersionsCsv() {
    //given
    LineVersion lineVersion1 = LineTestData.lineVersionBuilder().build();
    LineVersion lineVersion2 = LineTestData.lineVersionBuilder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("desc2")
        .build();
    List<LineVersion> lineVersions = List.of(lineVersion1, lineVersion2);
    when(lineVersionRepository.getFullLineVersions()).thenReturn(lineVersions);
    //when
    File result = lineVersionExportService.getFullVersionsCsv();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("full_");
    result.delete();
  }

  @Test
   void shouldGetActualVersionsCsv() {
    //given
    LineVersion lineVersion1 = LineTestData.lineVersionBuilder().build();
    LineVersion lineVersion2 = LineTestData.lineVersionBuilder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("desc2")
        .build();
    List<LineVersion> lineVersions = List.of(lineVersion1, lineVersion2);
    when(lineVersionRepository.getActualLineVersions(LocalDate.now())).thenReturn(lineVersions);
    //when
    File result = lineVersionExportService.getActualVersionsCsv();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("actual_date_");
    result.delete();
  }

  @Test
   void shouldGetFutureTimetableVersionsCsv() {
    //given
    LineVersion lineVersion1 = LineTestData.lineVersionBuilder().build();
    LineVersion lineVersion2 = LineTestData.lineVersionBuilder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("desc2")
        .build();
    List<LineVersion> lineVersions = List.of(lineVersion1, lineVersion2);
    when(lineVersionRepository.getActualLineVersions(LocalDate.now())).thenReturn(lineVersions);
    //when
    File result = lineVersionExportService.getFutureTimetableVersionsCsv();
    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isNotNull();
    assertThat(result.getName()).contains("future_timetable_");
    result.delete();
  }

  @Test
  void shouldThrowExportExceptionWhenDirDoesNotExists() {
    //given
    when(fileService.getDir()).thenReturn("." + File.separator + "test" + File.separator + "export" + File.separator);
    when(lineVersionRepository.getActualLineVersions(LocalDate.now())).thenReturn(
        Collections.emptyList());
    //when

    assertThatExceptionOfType(ExportException.class).isThrownBy(
        () -> lineVersionExportService.getFutureTimetableVersionsCsv());
  }

}