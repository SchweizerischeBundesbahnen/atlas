package ch.sbb.exportservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.FileExportService;
import java.io.InputStream;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.web.servlet.MvcResult;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExportPrmBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private FileExportService<PrmExportType> fileExportService;

  @Test
  @Order(1)
  void shouldGetJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

      doReturn(inputStreamResource).when(fileExportService)
          .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);

      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/stop-point-version/full")
              .contentType(contentType))
          .andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Test
  @Order(2)
  void shouldGetJsonUnsuccessfully() throws Exception {
    //given
    doThrow(FileException.class).when(fileExportService)
        .streamJsonFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/stop-point-version/full")
            .contentType(contentType)).andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(3)
  void shouldDownloadGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
      doReturn("service-point").when(fileExportService)
          .getBaseFileName(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/stop-point-version/full")
              .contentType(contentType)).andExpect(request().asyncStarted())
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
        .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);

    //when & then
    MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/stop-point-version/full")
            .contentType(contentType)).andExpect(request().asyncStarted())
        .andReturn();

    mvc.perform(asyncDispatch(mvcResult))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @Order(5)
  void shouldDownloadLatestGzipJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
      doReturn("prm/full/full_stop_point-2023-10-27.json.gz").when(fileExportService)
          .getLatestUploadedFileName(PrmBatchExportFileName.STOP_POINT_VERSION, PrmExportType.FULL);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/download-gzip-json/latest/stop-point-version/full")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/gzip"));
    }
  }

  @Test
  @Order(6)
  void shouldDownloadLatestJsonSuccessfully() throws Exception {
    //given
    try (InputStream inputStream = this.getClass().getResourceAsStream("/stop-point-data.json.gz")) {
      InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
      doReturn(inputStreamResource).when(fileExportService)
          .streamGzipFile(PrmExportType.FULL, PrmBatchExportFileName.STOP_POINT_VERSION);
      doReturn("prm/full/full_stop_point-2023-10-27.json.gz").when(fileExportService)
          .getLatestUploadedFileName(PrmBatchExportFileName.STOP_POINT_VERSION, PrmExportType.FULL);
      //when & then
      MvcResult mvcResult = mvc.perform(get("/v1/export/prm/json/latest/stop-point-version/full")
              .contentType(contentType)).andExpect(request().asyncStarted())
          .andReturn();

      mvc.perform(asyncDispatch(mvcResult))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"));
    }
  }
}
