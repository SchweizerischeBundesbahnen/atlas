package ch.sbb.exportservice.controller;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.FileExportService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExportPlatformBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private ExportPlatformJobService exportPlatformJobService;


  @Test
  @Order(6)
  void shouldPostPlatformExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportPlatformJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/platform-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

}
