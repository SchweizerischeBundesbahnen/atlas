package ch.sbb.exportservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.s3.config.AmazonBucket;
import ch.sbb.atlas.s3.service.AmazonFileStreamingService;
import ch.sbb.atlas.s3.service.AmazonService;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class FileExportServiceTest {

  private FileExportService fileExportService;

  @Mock
  private AmazonService amazonService;
  @Mock
  private AmazonFileStreamingService amazonFileStreamingService;

  @BeforeEach
  void init() {
    openMocks(this);
    fileExportService = new FileExportService(amazonFileStreamingService, amazonService);
  }

  @Test
  void shouldStreamGzipFile() {
    // given & when
    fileExportService.streamGzipFile("filename.json.gz");
    // then
    verify(amazonFileStreamingService).streamFile(AmazonBucket.EXPORT, "filename.json.gz");
  }

  @Test
  void shouldStreamXmlFile() {
    // Given
    final ExportFilePathV2 exportFilePathV2 = ExportFilePathV2.buildV2(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL);
    final StreamingResponseBody streamingResponseBody = mock(StreamingResponseBody.class);
    when(amazonFileStreamingService.streamZipEntry(AmazonBucket.EXPORT, exportFilePathV2.xmlZipFileToStream())).thenReturn(
        streamingResponseBody);

    // When
    final StreamingResponseBody result = fileExportService.streamXmlFile(exportFilePathV2);

    // Then
    assertThat(result).isEqualTo(streamingResponseBody);
    verify(amazonFileStreamingService).streamZipEntry(AmazonBucket.EXPORT, exportFilePathV2.xmlZipFileToStream());
  }

  @Test
  void shouldStreamLatestXmlFile() {
    // Given
    final ExportFilePathV2 exportFilePathV2 = ExportFilePathV2.buildV2(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL);
    final String latestUploadedFileName = "v2/ersad-netex/actual/ersad-netex-2025-01-01.xml.zip";
    final StreamingResponseBody streamingResponseBody = mock(StreamingResponseBody.class);
    when(amazonService.getLatestUploadedObject(any(), any(), any(), any())).thenReturn(latestUploadedFileName);
    when(amazonFileStreamingService.streamZipEntry(AmazonBucket.EXPORT, latestUploadedFileName)).thenReturn(
        streamingResponseBody);

    // When
    final StreamingResponseBody result = fileExportService.streamLatestXmlFile(exportFilePathV2);

    // Then
    assertThat(result).isEqualTo(streamingResponseBody);
    verify(amazonFileStreamingService).streamZipEntry(AmazonBucket.EXPORT, latestUploadedFileName);
  }

  @Test
  void shouldGetLatestUploadedXmlZipFileName() {
    // Given
    final ExportFilePathV2 exportFilePathV2 = ExportFilePathV2.buildV2(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL);
    final String latestUploadedFileName = "v2/ersad-netex/actual/ersad-netex-2025-01-01.xml.zip";
    when(amazonService.getLatestUploadedObject(AmazonBucket.EXPORT, exportFilePathV2.s3BucketDirPath(),
        exportFilePathV2.getPrefix(), ExportFilePathV2.XML_ZIP_EXTENSION)).thenReturn(latestUploadedFileName);

    // When
    final String result = fileExportService.getLatestUploadedXmlZipFileName(exportFilePathV2);

    // Then
    assertThat(result).isEqualTo(latestUploadedFileName);
  }
}
