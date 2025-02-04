package ch.sbb.exportservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

class BoDiFileStreamingControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private AmazonFileStreamingService amazonFileStreamingService;

  @MockBean
  private AmazonService amazonService;

  @Test
  void shouldGetTransportCompanyJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/transport-company-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFileAndDecompress(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/bodi/json/TRANSPORT_COMPANY/FULL")
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
  void shouldDownloadTransportCompanyGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/transport-company-data.json.gzip")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(amazonFileStreamingService)
          .streamFile(eq(AmazonBucket.EXPORT), anyString());
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/bodi/download-gzip-json/TRANSPORT_COMPANY/FULL")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"))
          .andExpect(result -> assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty());
    }
  }

}
