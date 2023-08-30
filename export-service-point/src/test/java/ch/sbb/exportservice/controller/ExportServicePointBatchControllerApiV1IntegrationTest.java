package ch.sbb.exportservice.controller;

import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

      doReturn(streamingResponseBody).when(fileExportService)
          .streamJsonFile(ExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);

      //when & then
      mvc.perform(get("/v1/export/json/service-point-version/world-full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(3)));
    }
  }

  @Test
  @Order(2)
  public void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileNotFoundException.class).when(fileExportService)
        .streamJsonFile(ExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);

    //when & then
    mvc.perform(get("/v1/export/json/service-point-version/world-full")
            .contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(3)
  public void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(ExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);
      doReturn("service-point").when(fileExportService)
          .getBaseFileName(ExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);
      //when & then
      mvc.perform(get("/v1/export/download-gzip-json/service-point-version/world-full")
              .contentType(contentType))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(4)
  public void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileNotFoundException.class).when(fileExportService)
        .streamGzipFile(ExportType.WORLD_FULL, BatchExportFileName.SERVICE_POINT_VERSION);

    //when & then
    mvc.perform(get("/v1/export/download-gzip-json/service-point-version/world-full")
            .contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(5)
  public void shouldNotDownloadJsonWhenExportTypeIsNotAllowedForTheExportFile() throws Exception {
    //given
    //when & then
    mvc.perform(get("/v1/export/download-gzip-json/traffic-point-element-version/swiss-only-full")
            .contentType(contentType))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message",
            is("Download file [TRAFFIC_POINT_ELEMENT_VERSION] with export type [SWISS_ONLY_FULL] not allowed!")))
        .andExpect(jsonPath("$.error",
            is("To download the file [TRAFFIC_POINT_ELEMENT_VERSION] are only allowed the following export types: [WORLD_FULL, "
                + "WORLD_ONLY_ACTUAL, WORLD_ONLY_TIMETABLE_FUTURE]")));
  }

  @Test
  @Order(6)
  public void shouldPostServicePointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/export/service-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  public void shouldPostTrafficPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/export/traffic-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  @Order(8)
  public void shouldPostLoadingPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(mailProducerService).produceMailNotification(any());

    //when & then
    mvc.perform(post("/v1/export/loading-point-batch")
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
