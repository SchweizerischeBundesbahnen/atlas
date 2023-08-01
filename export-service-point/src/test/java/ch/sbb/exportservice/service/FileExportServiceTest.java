package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class FileExportServiceTest {

  private FileExportService fileExportService;

  @Mock
  private AmazonService amazonService;

  @Mock
  private FileService fileService;

  @BeforeEach
  public void init() {
    openMocks(this);
    fileExportService = new FileExportService(amazonService, fileService);
  }

  @Test
  public void shouldStreamingJsonFile() throws IOException {
    //given
    File file = new File(this.getClass().getResource("/service-point.json.gzip").getFile());
    when(amazonService.pullFile(eq(AmazonBucket.EXPORT), any())).thenReturn(file);
    //when
    StreamingResponseBody result = fileExportService.streamingJsonFile(ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION);
    //then
    assertThat(result).isNotNull();

  }

  @Test
  public void shouldStreamingGzipFile() throws IOException {
    //given
    File file = new File(this.getClass().getResource("/service-point.json.gzip").getFile());
    when(amazonService.pullFile(eq(AmazonBucket.EXPORT), any())).thenReturn(file);
    //when
    StreamingResponseBody result = fileExportService.streamingGzipFile(ExportType.WORLD_FULL,ExportFileName.SERVICE_POINT_VERSION);
    //then
    assertThat(result).isNotNull();

  }

  @Test
  public void shouldPutGzipFile() throws IOException {
    //given
    File file = new File(this.getClass().getResource("/service-point.json.gzip").getFile());
    //when
    fileExportService.exportFile(file, ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION, ExportExtensionFileType.JSON_EXTENSION);
    //then
    verify(amazonService).putGzipFile(AmazonBucket.EXPORT, file, "service_point/full");
  }

  @Test
  public void shouldPutZipFile() throws IOException {
    //given
    File file = new File(this.getClass().getResource("/service-point-data.json").getFile());
    //when
    fileExportService.exportFile(file, ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION, ExportExtensionFileType.CSV_EXTENSION);
    //then
    verify(amazonService).putZipFile(AmazonBucket.EXPORT, file, "service_point/full");
  }

}