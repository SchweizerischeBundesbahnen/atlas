package ch.sbb.exportservice.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ObjectStreamClass;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExportFilePathV2Test {

  private ExportFilePathV2 exportFilePath;

  @Test
  void shouldExposePinnedSerialVersionUid() {
    // Given
    final long expectedSerialVersionUid = -1808687188997943332L;

    // When
    final long result = ObjectStreamClass.lookup(ExportFilePathV2.class).getSerialVersionUID();

    // Then
    assertThat(result).isEqualTo(expectedSerialVersionUid);
  }

  @Test
  void shouldBuildFileName() {
    // Given
    exportFilePath = ExportFilePathV2.getV2Builder(
            ExportObjectV2.SERVICE_POINT,
            ExportTypeV2.WORLD_TIMETABLE_YEARS)
        .actualDate(LocalDate.of(2025, 1, 1))
        .build();
    final String expectedFileName = "timetable-years-world-service-point-2025-01-01";

    // When
    final String result = exportFilePath.fileName();

    // Then
    assertThat(result).isEqualTo(expectedFileName);
  }

  @Test
  void shouldBuildFileToStreamPath() {
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
  void shouldBuildActualDateFilePath() {
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
  void shouldBuildS3BucketDirPath() {
    // Given
    exportFilePath = ExportFilePathV2.buildV2(ExportObjectV2.TRANSPORT_COMPANY, ExportTypeV2.SWISS_ACTUAL);
    final String expectedS3BucketDirPath = "v2/transport-company/actual-date";

    // When

    final String result = exportFilePath.s3BucketDirPath();

    // Then
    assertThat(result).isEqualTo(expectedS3BucketDirPath);
  }

  @Test
  void shouldBuildErsadNetexXmlFileName() {
    // Given
    exportFilePath = ExportFilePathV2.getV2Builder(
            ExportObjectV2.ERSAD_NETEX,
            ExportTypeV2.ACTUAL)
        .actualDate(LocalDate.of(2025, 1, 1))
        .systemDir("")
        .extension(ExportExtensionFileType.XML_EXTENSION.getExtension())
        .build();
    final String expectedFilePath = "actual-date-ersad-netex-2025-01-01.xml";

    // When
    final String result = exportFilePath.actualDateFilePath();

    // Then
    assertThat(result).isEqualTo(expectedFilePath);
  }

  @Test
  void shouldBuildErsadNetexS3BaseDir() {
    // Given
    exportFilePath = ExportFilePathV2.buildV2(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL);
    final String expectedS3BucketDirPath = "v2/ersad-netex/actual-date";

    // When
    final String result = exportFilePath.s3BucketDirPath();

    // Then
    assertThat(result).isEqualTo(expectedS3BucketDirPath);
  }

}
