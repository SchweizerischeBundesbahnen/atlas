package ch.sbb.exportservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import ch.sbb.exportservice.service.FileExportService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExportServicePointBatchControllerApiV1IntegrationTest extends BaseExportControllerTest {

  @MockBean
  private FileExportService<SePoDiExportType> fileExportService;

  @MockBean
  private ExportServicePointJobService exportServicePointJobService;

  @MockBean
  private ExportTrafficPointElementJobService exportTrafficPointElementJobService;

  @MockBean
  private ExportLoadingPointJobService exportLoadingPointJobService;

  @Test
  @Order(1)
  void shouldGetJsonSuccessfully() throws Exception {
    //given
    StreamingResponseBody streamingResponseBody = writeOutputStream(new ByteArrayInputStream(JSON_DATA.getBytes()));

    doReturn(streamingResponseBody).when(fileExportService)
        .streamJsonFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/json/service-point-version/world-full")
            .contentType(contentType)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  @Order(2)
  void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(fileExportService)
        .streamJsonFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/json/service-point-version/world-full")
            .contentType(contentType))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(3)
  void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
      doReturn("service-point").when(fileExportService)
          .getBaseFileName(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/service-point-version/world-full")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(4)
  void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(fileExportService)
        .streamGzipFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/service-point-version/world-full")
            .contentType(contentType))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(5)
  void shouldNotDownloadJsonWhenExportTypeIsNotAllowedForTheExportFile() throws Exception {
    //given
    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/traffic-point-element-version/swiss-only-full")
            .contentType(contentType))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
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
  void shouldPostServicePointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportServicePointJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/service-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void shouldPostTrafficPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportTrafficPointElementJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/traffic-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  @Order(8)
  void shouldPostLoadingPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportLoadingPointJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/loading-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  @Order(9)
  void shouldDownloadLatestGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
      doReturn("service_point/full/full-swiss-only-service_point-2023-09-30.csv.json").when(fileExportService)
          .getLatestUploadedFileName(SePoDiBatchExportFileName.SERVICE_POINT_VERSION, SePoDiExportType.WORLD_FULL);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/latest/service-point-version/world-full")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(10)
  void shouldDownloadLatestJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gz")) {
      StreamingResponseBody streamingResponseBody = writeOutputStream(inputStream);
      doReturn(streamingResponseBody).when(fileExportService)
          .streamGzipFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.SERVICE_POINT_VERSION);
      doReturn("service_point/full/full-swiss-only-service_point-2023-09-30.csv.json").when(fileExportService)
          .getLatestUploadedFileName(SePoDiBatchExportFileName.SERVICE_POINT_VERSION, SePoDiExportType.WORLD_FULL);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/latest/service-point-version/world-full")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"));
    }
  }

}
