package ch.sbb.exportservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.job.sepodi.servicepoint.service.ExportServicePointJobService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class ExportControllerApiV2Test extends BaseControllerApiTest {

  @MockitoSpyBean
  private ExportServicePointJobService exportServicePointJobService;

  @Test
  void startExport() throws Exception {
    // given
    Mockito.doNothing().when(exportServicePointJobService).startExportJobs();

    // when
    mvc.perform(post("/v2/export/sepodi/service-point-batch")
            .contentType(contentType)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    Mockito.verify(exportServicePointJobService).startExportJobs();
  }

}
