package ch.sbb.exportservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExportFilePathV1Test {

  @Test
  void shouldGetFileNameWithoutPrefix() {
    // given & when
    ExportFilePathV1 exportFilePathV1 = new ExportFilePathV1(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION, "",
        ExportExtensionFileType.JSON_EXTENSION, LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePathV1.fileName()).isEqualTo("full-stop_point-2024-03-10");
  }

  @Test
  void shouldGetFileNameWithPrefix() {
    // given & when
    ExportFilePathV1 exportFilePathV1 = new ExportFilePathV1(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, "", ExportExtensionFileType.CSV_EXTENSION, LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePathV1.fileName()).isEqualTo("actual_date-swiss-only-service_point-2024-03-10");
  }

  @Test
  void shouldGetFileToStream() {
    // given & when
    ExportFilePathV1 exportFilePathV1 = new ExportFilePathV1(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, "", ExportExtensionFileType.JSON_EXTENSION, LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePathV1.fileToStream()).isEqualTo(
        "service_point/actual_date/actual_date-swiss-only-service_point-2024-03-10.json.gz");
  }

  @Test
  void shouldGetActualDateFilePath() {
    // given & when
    ExportFilePathV1 exportFilePathV1 = new ExportFilePathV1(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, "/path/export/", ExportExtensionFileType.CSV_EXTENSION,
        LocalDate.of(2024, 3, 10));
    // then
    assertThat(exportFilePathV1.actualDateFilePath()).isEqualTo(
        "/path/export/actual_date-swiss-only-service_point-2024-03-10.csv");
  }

  @Test
  void shouldGetS3BucketDirPath() {
    // given & when
    ExportFilePathV1 exportFilePathV1 = new ExportFilePathV1(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
    // then
    assertThat(exportFilePathV1.s3BucketDirPath()).isEqualTo("service_point/actual_date");
  }

}
