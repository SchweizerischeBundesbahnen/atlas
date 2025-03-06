package ch.sbb.exportservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExportFilePathV2Test {

  private ExportFilePathV2 exportFilePath;

  @Test
  void fileName() {
    // Given
    exportFilePath = ExportFilePathV2.getV2Builder(
            ExportObjectV2.SERVICE_POINT,
            ExportTypeV2.WORLD_FUTURE_TIMETABLE)
        .actualDate(LocalDate.of(2025, 1, 1))
        .build();
    final String expectedFileName = "future-timetable-world-service-point-2025-01-01";

    // When
    final String result = exportFilePath.fileName();

    // Then
    assertThat(result).isEqualTo(expectedFileName);
  }

  @Test
  void fileToStream() {
    // Given
    exportFilePath = ExportFilePathV2.getV2Builder(
            ExportObjectV2.SUBLINE,
            ExportTypeV2.ACTUAL)
        .actualDate(LocalDate.of(2025, 1, 1))
        .build();
    final String expectedFileToStream = "v2/subline/actual-date/actual-date-subline-2025-01-01.json.gz";

    // When
    final String result = exportFilePath.fileToStream();

    // Then
    assertThat(result).isEqualTo(expectedFileToStream);
  }

  @Test
  void actualDateFilePath() {
    // Given
    exportFilePath = ExportFilePathV2.getV2Builder(
            ExportObjectV2.LINE,
            ExportTypeV2.WORLD_FULL)
        .actualDate(LocalDate.of(2025, 1, 1))
        .systemDir("tmp/")
        .extension(".csv")
        .build();
    final String expectedFilePath = "tmp/full-world-line-2025-01-01.csv";

    // When
    final String result = exportFilePath.actualDateFilePath();

    // Then
    assertThat(result).isEqualTo(expectedFilePath);
  }

  @Test
  void s3BucketDirPath() {
    // Given
    exportFilePath = ExportFilePathV2.buildV2(ExportObjectV2.TRANSPORT_COMPANY, ExportTypeV2.SWISS_ACTUAL);
    final String expectedS3BucketDirPath = "v2/transport-company/actual-date";

    // When
    final String result = exportFilePath.s3BucketDirPath();

    // Then
    assertThat(result).isEqualTo(expectedS3BucketDirPath);
  }

}
