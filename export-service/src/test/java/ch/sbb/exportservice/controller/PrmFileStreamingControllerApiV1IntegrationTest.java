package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.test.web.servlet.MvcResult;

class PrmFileStreamingControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private AmazonFileStreamingService amazonFileStreamingService;

  @MockBean
  private AmazonService amazonService;

  @Test
  void shouldDownloadGzipJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(amazonFileStreamingService)
        .streamFile(eq(AmazonBucket.EXPORT), anyString());
    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/STOP_POINT_VERSION/FULL")
            .contentType(contentType)).andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(amazonFileStreamingService)
        .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/STOP_POINT_VERSION/FULL")
            .contentType(contentType)).andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldGetActualJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/STOP_POINT_VERSION/ACTUAL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetFutureTimetableJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/STOP_POINT_VERSION/TIMETABLE_FUTURE")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetStopPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/STOP_POINT_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadStopPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/STOP_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestStopPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("stop_point/full/full-stop_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "stop_point/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "stop_point/full/full-stop_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/STOP_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestStopPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("stop_point/full/full-stop_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "stop_point/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "stop_point/full/full-stop_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/STOP_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetPlatformJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/PLATFORM_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadPlatformGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/PLATFORM_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestPlatformGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("platform/full/full-platform-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "platform/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "platform/full/full-platform-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/PLATFORM_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestPlatformJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/platform-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("platform/full/full-platform-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "platform/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "platform/full/full-platform-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/PLATFORM_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetReferencePointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/reference-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/REFERENCE_POINT_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadReferencePointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/reference-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/REFERENCE_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestReferencePointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/reference-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("reference_point/full/full-reference_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "reference_point/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "reference_point/full/full-reference_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/REFERENCE_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestReferencePointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/reference-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("reference_point/full/full-reference_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "reference_point/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "reference_point/full/full-reference_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/REFERENCE_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetContactPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/contact-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/CONTACT_POINT_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadContactPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/contact-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/CONTACT_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestContactPointGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/contact-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("contact_point/full/full-contact_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "contact_point/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "contact_point/full/full-contact_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/CONTACT_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestContactPointJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/contact-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("contact_point/full/full-contact_point-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "contact_point/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "contact_point/full/full-contact_point-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/CONTACT_POINT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetToiletJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/toilet-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/TOILET_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldDownloadToiletGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/toilet-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/TOILET_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestToiletGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/toilet-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("toilet/full/full-toilet-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "toilet/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "toilet/full/full-toilet-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/TOILET_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestToiletJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/toilet-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("toilet/full/full-toilet-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "toilet/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "toilet/full/full-toilet-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/TOILET_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  void shouldGetParkingLotJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/parking-lot-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/PARKING_LOT_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1043)));
    }
  }

  @Test
  void shouldDownloadParkingLotGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/parking-lot-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/PARKING_LOT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestParkingLotGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/parking-lot-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("parking_lot/full/full-parking_lot-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "parking_lot/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "parking_lot/full/full-parking_lot-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/PARKING_LOT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestParkingLotJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/parking-lot-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("parking_lot/full/full-parking_lot-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "parking_lot/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "parking_lot/full/full-parking_lot-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/PARKING_LOT_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(1043)));
    }
  }

  @Test
  void shouldGetRelationJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/relation-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/RELATION_VERSION/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(10)));
    }
  }

  @Test
  void shouldDownloadRelationGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/relation-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/RELATION_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestRelationGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/relation-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("relation/full/full-relation-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "relation/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(AmazonBucket.EXPORT, "relation/full/full-relation-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/RELATION_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

  @Test
  void shouldDownloadLatestRelationJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/relation-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn("relation/full/full-relation-2023-10-27.json.gz").when(amazonService)
          .getLatestJsonUploadedObject(AmazonBucket.EXPORT, "relation/full", "");
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(AmazonBucket.EXPORT, "relation/full/full-relation-2023-10-27.json.gz");
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/RELATION_VERSION/FULL")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();
      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$", hasSize(10)));
    }
  }

}
