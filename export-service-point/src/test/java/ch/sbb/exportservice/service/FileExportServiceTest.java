package ch.sbb.exportservice.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
  void shouldStreamJsonFilePRM() {
    // given & when
    fileExportService.streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
    // then
    verify(amazonFileStreamingService).streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
  }

  @Test
  void shouldStreamJsonFileSePoDi() {
    // given & when
    fileExportService.streamJsonFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
    // then
    verify(amazonFileStreamingService).streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
  }

  @Test
  void shouldStreamLatestJsonFile() {
    // given
    when(amazonService.getLatestJsonUploadedObject(eq(AmazonBucket.EXPORT), anyString(), anyString()))
        .thenReturn("filename.json.gz");
    // when
    fileExportService.streamLatestJsonFile(PrmExportType.ACTUAL, PrmBatchExportFileName.CONTACT_POINT_VERSION);
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
    // given & when
    fileExportService.getLatestUploadedFileName(PrmExportType.ACTUAL, PrmBatchExportFileName.CONTACT_POINT_VERSION);
    // then
    verify(amazonService).getLatestJsonUploadedObject(AmazonBucket.EXPORT, "contact_point/actual-date", "");
  }

}
