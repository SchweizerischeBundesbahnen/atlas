package ch.sbb.exportservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.FileExportService;
import ch.sbb.exportservice.service.MailProducerService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@BatchDataSourceConfigTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExportServicePointBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private MailProducerService mailProducerService;
  @MockBean
  private FileExportService fileExportService;

  @Test
  @Order(1)
  public void shouldGetJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point-data.json")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);

      doReturn(streamingResponseBody).when(fileExportService).streamingJsonFile(ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION);

      //when & then
      mvc.perform(get("/v1/export/json/world-full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(3)));
    }
  }

  @Test
  @Order(2)
  public void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileNotFoundException.class).when(fileExportService).streamingJsonFile(ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION);

    //when & then
    mvc.perform(get("/v1/export/json/world-full")
            .contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(3)
  public void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService).streamingGzipFile(ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION);
      doReturn("service-point").when(fileExportService).getBaseFileName(ExportType.WORLD_FULL, ExportFileName.SERVICE_POINT_VERSION);
      //when & then
      mvc.perform(get("/v1/export/download-gzip-json/world-full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(4)
  public void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileNotFoundException.class).when(fileExportService).streamingGzipFile(ExportType.WORLD_FULL,ExportFileName.SERVICE_POINT_VERSION);

    //when & then
    mvc.perform(get("/v1/export/download-gzip-json/world-full")
            .contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(5)
  public void shouldPostServicePointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());
    //when & then
    mvc.perform(post("/v1/export/batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  private StreamingResponseBody writeOutputStream(InputStream inputStream) {
    return outputStream -> {
      int len;
      byte[] data = new byte[4096];
      while ((len = inputStream.read(data, 0, data.length)) != -1) {
        outputStream.write(data, 0, len);
      }
      inputStream.close();
    };
  }

}
