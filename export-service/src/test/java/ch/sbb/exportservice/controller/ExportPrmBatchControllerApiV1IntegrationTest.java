package ch.sbb.exportservice.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.service.ExportContactPointJobService;
import ch.sbb.exportservice.service.ExportParkingLotJobService;
import ch.sbb.exportservice.service.ExportPlatformJobService;
import ch.sbb.exportservice.service.ExportReferencePointJobService;
import ch.sbb.exportservice.service.ExportRelationJobService;
import ch.sbb.exportservice.service.ExportStopPointJobService;
import ch.sbb.exportservice.service.ExportToiletJobService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class ExportPrmBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

  @MockBean
  private ExportStopPointJobService exportStopPointJobService;

  @MockBean
  private ExportPlatformJobService exportPlatformJobService;

  @MockBean
  private ExportReferencePointJobService exportReferencePointJobService;

  @MockBean
  private ExportContactPointJobService exportContactPointJobService;

  @MockBean
  private ExportToiletJobService exportToiletJobService;

  @MockBean
  private ExportParkingLotJobService exportParkingLotJobService;

  @MockBean
  private ExportRelationJobService exportRelationJobService;

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

  @Test
  void shouldPostToiletExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportToiletJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/toilet-batch").contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  void shouldPostParkingLotExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportParkingLotJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/parking-lot-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  void shouldPostRelationExportBatchSuccessfully() throws Exception {
    //given
    doNothing().when(exportRelationJobService).startExportJobs();

    //when & then
    mvc.perform(post("/v1/export/prm/relation-batch")
            .contentType(contentType))
        .andExpect(status().isOk());
  }

}
