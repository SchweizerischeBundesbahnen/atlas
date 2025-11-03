package ch.sbb.exportservice.service;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
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
  void shouldStreamGzipFile() {
    // given & when
    fileExportService.streamGzipFile("filename.json.gz");
    // then
    verify(amazonFileStreamingService).streamFile(AmazonBucket.EXPORT, "filename.json.gz");
  }
}
