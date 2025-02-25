package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class FileStreamingControllerApiV1DELETEIntegrationTest extends BaseControllerApiTest {

  @MockBean
  private AmazonFileStreamingService amazonFileStreamingService;

  @MockBean
  private AmazonService amazonService;

  @Test
  void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(amazonFileStreamingService)
        .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/json/SERVICE_POINT_VERSION/WORLD_FULL")
            .contentType(contentType))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(amazonFileStreamingService)
        .streamFile(eq(AmazonBucket.EXPORT), anyString());

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/SERVICE_POINT_VERSION/WORLD_FULL")
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
    MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/TRAFFIC_POINT_ELEMENT_VERSION/SWISS_ONLY_FULL")
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
  void shouldGetServicePointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/SERVICE_POINT_VERSION/WORLD_FULL")
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
  void shouldDownloadServicePointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gzip")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/SERVICE_POINT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestServicePointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point.json.gzip")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("service_point/full/full-world-service_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "service_point/full", "world");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "service_point/full/full-world-service_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/latest/SERVICE_POINT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestServicePointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/service-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("service_point/full/full-world-service_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "service_point/full", "world");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "service_point/full/full-world-service_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/latest/SERVICE_POINT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(3)));
    }
  }

  @Test
  void shouldGetTrafficPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/traffic-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/TRAFFIC_POINT_ELEMENT_VERSION/WORLD_FULL")
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
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/TRAFFIC_POINT_ELEMENT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestTrafficPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/traffic-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("traffic_point/full/full-world-traffic_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "traffic_point/full", "world");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "traffic_point/full/full-world-traffic_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/latest/TRAFFIC_POINT_ELEMENT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestTrafficPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/traffic-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("traffic_point/full/full-world-traffic_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "traffic_point/full", "world");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "traffic_point/full/full-world-traffic_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/latest/TRAFFIC_POINT_ELEMENT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetLoadingPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/loading-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/LOADING_POINT_VERSION/WORLD_FULL")
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
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/LOADING_POINT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestLoadingPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/loading-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("loading_point/full/full-world-loading_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "loading_point/full", "world");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "loading_point/full/full-world-loading_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/download-gzip-json/latest/LOADING_POINT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestLoadingPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/loading-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("loading_point/full/full-world-loading_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "loading_point/full", "world");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "loading_point/full/full-world-loading_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/json/latest/LOADING_POINT_VERSION/WORLD_FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

}
