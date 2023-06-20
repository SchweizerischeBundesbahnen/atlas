package ch.sbb.exportservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.service.FileExportService;
import ch.sbb.exportservice.service.MailProducerService;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExportServicePointBatchControllerApiV1Test extends BaseControllerApiTest {

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
      MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.ISO_8859_1);

      doReturn(streamingResponseBody).when(fileExportService).streamingJsonFile(ServicePointExportType.WORLD_FULL);

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
    doThrow(FileNotFoundException.class).when(fileExportService).streamingJsonFile(ServicePointExportType.WORLD_FULL);

    //when & then
    mvc.perform(get("/v1/export/json/world-full")
            .contentType(contentType))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(3)
  public void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gzip")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(),
          StandardCharsets.ISO_8859_1);
      doReturn(streamingResponseBody).when(fileExportService).streamingGzipFile(ServicePointExportType.WORLD_FULL);
      doReturn("service-point").when(fileExportService).getBaseFileName(ServicePointExportType.WORLD_FULL);
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
    doThrow(FileNotFoundException.class).when(fileExportService).streamingGzipFile(ServicePointExportType.WORLD_FULL);

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
