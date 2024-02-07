package ch.sbb.exportservice.controller;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import ch.sbb.exportservice.service.FileExportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServicePointBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private FileExportService<SePoDiExportType> fileExportService;

  @Test
  void shouldGetJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

      doReturn(inputStreamResource).when(fileExportService)
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
  }

  @Test
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
  void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gzip")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
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
  void shouldDownloadLatestGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gzip")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
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
  void shouldDownloadLatestJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gzip")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
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

  @Test
  void shouldGetTrafficPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/traffic-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
              .streamJsonFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/traffic-point-element-version/world-full")
                      .contentType(contentType)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(request().asyncStarted())
              .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadTrafficPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/traffic-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
              .streamGzipFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
      doReturn("traffic-point").when(fileExportService)
              .getBaseFileName(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.TRAFFIC_POINT_ELEMENT_VERSION);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/traffic-point-element-version/world-full")
                      .contentType(contentType))
              .andExpect(request().asyncStarted())
              .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  void shouldGetLoadingPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/loading-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
              .streamJsonFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/loading-point-version/world-full")
                      .contentType(contentType)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(request().asyncStarted())
              .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadLoadingPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/loading-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
              .streamGzipFile(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
      doReturn("loading-point").when(fileExportService)
              .getBaseFileName(SePoDiExportType.WORLD_FULL, SePoDiBatchExportFileName.LOADING_POINT_VERSION);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/loading-point-version/world-full")
                      .contentType(contentType))
              .andExpect(request().asyncStarted())
              .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
              .andExpect(status().isOk())
              .andExpect(content().contentType("application/gzip"));
    }
  }

}
