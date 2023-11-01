package ch.sbb.exportservice.service;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileServiceImpl;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class FileExportServiceTest {

  private FileExportService<ExportTypeBase> fileExportService;

  @Mock
  private AmazonService amazonService;
  @Mock
  private AmazonFileStreamingService amazonFileStreamingService;

  private final FileServiceImpl fileService = new FileServiceImpl();

  @BeforeEach
   void init() {
    openMocks(this);
    fileExportService = new FileExportService<>(amazonFileStreamingService, amazonService, fileService);
  }

  @Test
  void shouldPutGzipFile() throws IOException {
    //given
    File file = new File(this.getClass().getResource("/service-point.json.gzip").getFile());
    //when
    fileExportService.exportFile(file, SePoDiExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION,
        ExportExtensionFileType.JSON_EXTENSION);
    //then
    verify(amazonService).putGzipFile(AmazonBucket.EXPORT, file, "service_point/full");
  }

  @Test
  void shouldPutZipFile() throws IOException {
    //given
    File file = new File(this.getClass().getResource("/service-point-data.json").getFile());
    //when
    fileExportService.exportFile(file, SePoDiExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION,
        ExportExtensionFileType.CSV_EXTENSION);
    //then
    verify(amazonService).putZipFile(AmazonBucket.EXPORT, file, "service_point/full");
  }

  @Test
  void shouldStreamJsonFileWhileDecompressing() {
    fileExportService.streamJsonFile(SePoDiExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);
    verify(amazonFileStreamingService).streamFileAndDecompress(AmazonBucket.EXPORT,
        "service_point/full/full-world-service_point-" + LocalDate.now() + ".json.gz");
  }

  @Test
  void shouldStreamGzipFile() {
    fileExportService.streamGzipFile(SePoDiExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);
    verify(amazonFileStreamingService).streamFile(AmazonBucket.EXPORT,
        "service_point/full/full-world-service_point-" + LocalDate.now() + ".json.gz");
  }

}
