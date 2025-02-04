package ch.sbb.exportservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExportFilePathTest {

  @Test
  void shouldGetActualDateFileNameWithoutPrefix() {
    // given & when
    ExportFilePath exportFilePath = new ExportFilePath(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION, "",
        ExportExtensionFileType.JSON_EXTENSION, LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePath.actualDateFileName()).isEqualTo("full-stop_point-2024-03-10");
  }

  @Test
  void shouldGetActualDateFileNameWithPrefix() {
    // given & when
    ExportFilePath exportFilePath = new ExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, "", ExportExtensionFileType.CSV_EXTENSION, LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePath.actualDateFileName()).isEqualTo("actual_date-swiss-only-service_point-2024-03-10");
  }

  @Test
  void shouldGetFileToStream() {
    // given & when
    ExportFilePath exportFilePath = new ExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, "", ExportExtensionFileType.JSON_EXTENSION, LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePath.fileToStream()).isEqualTo(
        "service_point/actual_date/actual_date-swiss-only-service_point-2024-03-10.json.gz");
  }

  @Test
  void shouldGetActualDateFilePath() {
    // given & when
    ExportFilePath exportFilePath = new ExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, "/path/export/", ExportExtensionFileType.CSV_EXTENSION,
        LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePath.actualDateFilePath()).isEqualTo("/path/export/actual_date-swiss-only-service_point-2024-03-10.csv");
  }

  @Test
  void shouldGetS3BucketDirPath() {
    // given & when
    ExportFilePath exportFilePath = new ExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
    // then
    assertThat(exportFilePath.s3BucketDirPath()).isEqualTo("service_point/actual_date");
  }

}
