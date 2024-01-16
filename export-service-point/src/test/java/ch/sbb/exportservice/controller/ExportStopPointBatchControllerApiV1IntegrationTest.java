package ch.sbb.exportservice.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.service.ExportReferencePointJobService;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExportStopPointBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private ExportStopPointJobService exportStopPointJobService;
  @MockBean
  private ExportReferencePointJobService exportReferencePointJobService;


  @Test
  void shouldPostStopPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportStopPointJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/stop-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  void shouldPostReferencePointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportReferencePointJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/reference-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

}
