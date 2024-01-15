package ch.sbb.exportservice.controller;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.FileExportService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExportPlatformBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private FileExportService<PrmExportType> fileExportService;

  @MockBean
  private ExportPlatformJobService exportPlatformJobService;

  @Test
  @Order(1)
  void shouldGetJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);

      doReturn(streamingResponseBody).when(fileExportService)
          .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);

      //when & then
      mvc.perform(get("/v1/export/prm/platform/json/platform-version/full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  @Order(2)
  void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(fileExportService)
        .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);

    //when & then
    mvc.perform(get("/v1/export/prm/platform/json/platform-version/full")
            .contentType(contentType))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(3)
  void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
      doReturn("service-point").when(fileExportService)
          .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
      //when & then
      mvc.perform(get("/v1/export/prm/platform/download-gzip-json/platform-version/full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(4)
  void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(fileExportService)
        .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);

    //when & then
    mvc.perform(get("/v1/export/prm/platform/download-gzip-json/platform-version/full")
            .contentType(contentType))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(5)
  void shouldDownloadLatestGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
      doReturn("prm/full/full_platform-2023-10-27.json.gz").when(fileExportService)
          .getLatestUploadedFileName(PrmBatchExportFileName.PLATFORM_VERSION, PrmExportType.FULL);
      //when & then
      mvc.perform(get("/v1/export/prm/platform/download-gzip-json/latest/platform-version/full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(6)
  void shouldPostPlatformExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportPlatformJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/platform/platform-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void shouldDownloadLatestJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.PLATFORM_VERSION);
      doReturn("prm/full/full_platform-2023-10-27.json.gz").when(fileExportService)
          .getLatestUploadedFileName(PrmBatchExportFileName.PLATFORM_VERSION, PrmExportType.FULL);
      //when & then
      mvc.perform(get("/v1/export/prm/platform/json/latest/platform-version/full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"));
    }
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
