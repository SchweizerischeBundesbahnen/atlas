package ch.sbb.exportservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import ch.sbb.exportservice.service.FileExportService;
import java.io.InputStream;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class ExportStopPointBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private FileExportService<PrmExportType> fileExportService;

  @MockBean
  private ExportStopPointJobService exportStopPointJobService;

  @Test
  @Order(1)
   void shouldGetJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);

      doReturn(streamingResponseBody).when(fileExportService)
          .streamJsonFile(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);

      //when & then
      mvc.perform(get("/v1/export/prm/json/stop-point-version/full")
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
        .streamJsonFile(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);

    //when & then
    mvc.perform(get("/v1/export/prm/json/stop-point-version/full")
            .contentType(contentType))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(3)
   void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);
      doReturn("service-point").when(fileExportService)
          .getBaseFileName(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);
      //when & then
      mvc.perform(get("/v1/export/prm/download-gzip-json/stop-point-version/full")
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
        .streamGzipFile(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);

    //when & then
    mvc.perform(get("/v1/export/prm/download-gzip-json/stop-point-version/full")
            .contentType(contentType))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(5)
  void shouldDownloadLatestGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);
      doReturn("prm/full/full_stop_point-2023-10-27.json.gz").when(fileExportService)
          .getLatestUploadedFileName("prm/full",PrmExportType.FULL.getFileTypePrefix());
      //when & then
      mvc.perform(get("/v1/export/prm/download-gzip-json/latest/stop-point-version/full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(6)
  void shouldPostStopPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportStopPointJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/stop-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }


  @Test
  @Order(7)
  void shouldDownloadLatestJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, BatchExportFileName.STOP_POINT_VERSION);
      doReturn("prm/full/full_stop_point-2023-10-27.json.gz").when(fileExportService)
          .getLatestUploadedFileName("prm/full",PrmExportType.FULL.getFileTypePrefix());
      //when & then
      mvc.perform(get("/v1/export/prm/json/latest/stop-point-version/full")
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
