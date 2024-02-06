package ch.sbb.exportservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.service.ExportContactPointJobService;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.ExportReferencePointJobService;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExportStopPointBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private ExportStopPointJobService exportStopPointJobService;

  @MockBean
  private ExportPlatformJobService exportPlatformJobService;

  @MockBean
  private ExportReferencePointJobService exportReferencePointJobService;

  @MockBean
  private ExportContactPointJobService exportContactPointJobService;


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
  void shouldPostPlatformExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportPlatformJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/platform-batch")
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

  @Test
  void shouldPostContactPointExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportContactPointJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/contact-point-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

}
