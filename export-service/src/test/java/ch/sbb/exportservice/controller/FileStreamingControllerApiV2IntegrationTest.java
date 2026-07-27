package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonFileStreamingService;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.ContentLength;
import ch.sbb.atlas.amazon.service.StreamedFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

class FileStreamingControllerApiV2IntegrationTest extends BaseControllerApiTest {

  @MockitoBean
  private AmazonFileStreamingService amazonFileStreamingService;

  @MockitoBean
  private AmazonService amazonService;

  @BeforeEach
  void setUp() {
    when(amazonService.getLatestJsonUploadedObject(eq(AmazonBucket.EXPORT), anyString(), anyString()))
        .thenReturn("service-point-data.json");
  }

  @Test
  void shouldGetServicePointJsonSuccessfully() throws Exception {
    //given
    byte[] data = Objects.requireNonNull(this.getClass().getResourceAsStream("/service-point-data.json")).readAllBytes();
    StreamedFile streamedFile = new StreamedFile(new InputStreamResource(new ByteArrayInputStream(data)), ContentLength.of(data.length));
    doReturn(streamedFile).when(amazonFileStreamingService)
        .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v2/export/json/SERVICE_POINT/WORLD_FULL")
            .contentType(contentType)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, data.length))
        .andExpect(header().doesNotExist(HttpHeaders.TRANSFER_ENCODING))
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  void shouldGetLatestServicePointJsonSuccessfully() throws Exception {
    //given
    byte[] data = Objects.requireNonNull(this.getClass().getResourceAsStream("/service-point-data.json")).readAllBytes();
    StreamedFile streamedFile = new StreamedFile(new InputStreamResource(new ByteArrayInputStream(data)), ContentLength.of(data.length));
    doReturn(streamedFile).when(amazonFileStreamingService)
        .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v2/export/json/latest/SERVICE_POINT/WORLD_FULL")
            .contentType(contentType)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, data.length))
        .andExpect(header().doesNotExist(HttpHeaders.TRANSFER_ENCODING))
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  void shouldDownloadServicePointGzipJsonSuccessfully() throws Exception {
    //given
    byte[] data = Objects.requireNonNull(this.getClass().getResourceAsStream("/service-point.json.gzip")).readAllBytes();
    StreamedFile streamedFile = new StreamedFile(new InputStreamResource(new ByteArrayInputStream(data)), ContentLength.of(data.length));
    doReturn(streamedFile).when(amazonFileStreamingService)
        .streamFile(eq(AmazonBucket.EXPORT), anyString());

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v2/export/download-gzip-json/SERVICE_POINT/WORLD_FULL")
            .contentType(contentType))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/gzip"))
        .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, data.length))
        .andExpect(header().doesNotExist(HttpHeaders.TRANSFER_ENCODING))
        .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
  }

  @Test
  void shouldDownloadLatestServicePointGzipJsonSuccessfully() throws Exception {
    //given
    byte[] data = Objects.requireNonNull(this.getClass().getResourceAsStream("/service-point.json.gzip")).readAllBytes();
    StreamedFile streamedFile = new StreamedFile(new InputStreamResource(new ByteArrayInputStream(data)), ContentLength.of(data.length));
    doReturn(streamedFile).when(amazonFileStreamingService)
        .streamFile(eq(AmazonBucket.EXPORT), anyString());

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v2/export/download-gzip-json/latest/SERVICE_POINT/WORLD_FULL")
            .contentType(contentType))
        .andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/gzip"))
        .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, data.length))
        .andExpect(header().doesNotExist(HttpHeaders.TRANSFER_ENCODING))
        .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
  }
}
