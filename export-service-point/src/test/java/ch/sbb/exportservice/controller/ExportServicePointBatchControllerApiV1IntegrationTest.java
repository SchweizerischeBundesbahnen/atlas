package ch.sbb.exportservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.service.ExportServicePointJobService;
import ch.sbb.exportservice.service.ExportTrafficPointElementJobService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExportServicePointBatchControllerApiV1IntegrationTest extends BaseControllerApiTest {

    @MockBean
    private ExportServicePointJobService exportServicePointJobService;

    @MockBean
    private ExportTrafficPointElementJobService exportTrafficPointElementJobService;

    @MockBean
    private ExportLoadingPointJobService exportLoadingPointJobService;

    @Test
    void shouldPostServicePointExportBatchSuccessfully() throws Exception {
        //given
        doNothing().when(exportServicePointJobService).startExportJobs();

        //when & then
        mvc.perform(post("/v1/export/service-point-batch")
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPostTrafficPointExportBatchSuccessfully() throws Exception {
        //given
        doNothing().when(exportTrafficPointElementJobService).startExportJobs();

        //when & then
        mvc.perform(post("/v1/export/traffic-point-batch")
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPostLoadingPointExportBatchSuccessfully() throws Exception {
        //given
        doNothing().when(exportLoadingPointJobService).startExportJobs();

        //when & then
        mvc.perform(post("/v1/export/loading-point-batch")
                        .contentType(contentType))
                .andExpect(status().isOk());
    }

}
