package ch.sbb.exportservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExportPlatformBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private ExportPlatformJobService exportPlatformJobService;


  @Test
  void shouldPostPlatformExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportPlatformJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/platform-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

}
