package ch.sbb.exportservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class FileExportServiceTest {

  private FileExportService fileExportService;

  @Mock
  private AmazonService amazonService;
  @Mock
  private AmazonFileStreamingService amazonFileStreamingService;
  @Mock
  private FileService fileService;
  @Mock
  private Clock clock;

  @BeforeEach
  void init() {
    openMocks(this);
    doReturn(Instant.parse("2024-03-10T00:00:00Z")).when(clock).instant();
    doReturn(ZoneId.of("UTC")).when(clock).getZone();
    fileExportService = new FileExportService(amazonFileStreamingService, amazonService, fileService, clock);
  }

  @Test
  void shouldStreamJsonFile() {
    // given & when
    fileExportService.streamJsonFile("filename.json.gz");
    // then
    verify(amazonFileStreamingService).streamFileAndDecompress(AmazonBucket.EXPORT, "filename.json.gz");
  }

  @Test
  void shouldStreamGzipFile() {
    // given & when
    fileExportService.streamGzipFile("filename.json.gz");
    // then
    verify(amazonFileStreamingService).streamFile(AmazonBucket.EXPORT, "filename.json.gz");
  }

  @Test
  void shouldGetLatestUploadedFileName() {
    // given
    ExportFilePath exportFilePath = ExportFilePath.builder()
        .baseDir("baseDir")
        .dir("dir")
        .prefix("prefix")
        .build();
    // when
    fileExportService.getLatestUploadedFileName(exportFilePath);
    // then
    verify(amazonService).getLatestJsonUploadedObject(AmazonBucket.EXPORT, "baseDir/dir", "prefix");
  }

  @Test
  void shouldGetActualDateFileNameWithoutPrefix() {
    // given & when
    ExportFilePath exportFilePath = fileExportService.createExportFilePath(PrmExportType.FULL,
        PrmBatchExportFileName.STOP_POINT_VERSION);
    // then
    assertThat(exportFilePath.actualDateFileName()).isEqualTo("full-stop_point-2024-03-10");
  }

  @Test
  void shouldGetActualDateFileNameWithPrefix() {
    // given & when
    ExportFilePath exportFilePath = fileExportService.createExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
    // then
    assertThat(exportFilePath.actualDateFileName()).isEqualTo("actual_date-swiss-only-service_point-2024-03-10");
  }

  @Test
  void shouldGetFileToStream() {
    // given & when
    ExportFilePath exportFilePath = fileExportService.createExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
    // then
    assertThat(exportFilePath.getFileToStream()).isEqualTo(
        "service_point/actual_date/actual_date-swiss-only-service_point-2024-03-10.json.gz");
  }

  @Test
  void shouldGetActualDateFilePath() {
    // given
    doReturn("/path/export/").when(fileService).getDir();
    // when
    ExportFilePath exportFilePath = fileExportService.createExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION, ExportExtensionFileType.CSV_EXTENSION);
    // then
    assertThat(exportFilePath.actualDateFilePath()).isEqualTo("/path/export/actual_date-swiss-only-service_point-2024-03-10.csv");
  }

  @Test
  void shouldGetS3BucketDirPath() {
    // given & when
    ExportFilePath exportFilePath = fileExportService.createExportFilePath(SePoDiExportType.SWISS_ONLY_ACTUAL,
        SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
    // then
    assertThat(exportFilePath.s3BucketDirPath()).isEqualTo("service_point/actual_date");
  }

}
