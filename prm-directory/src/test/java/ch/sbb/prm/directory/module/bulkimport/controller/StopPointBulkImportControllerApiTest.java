package ch.sbb.prm.directory.module.bulkimport.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.prm.directory.module.bulkimport.service.StopPointBulkImportService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class StopPointBulkImportControllerApiTest extends BaseControllerApiTest {

  private static final String TERMINATE_STOP_POINT_PATH = "/internal/stop-point/bulk-import/terminate-stop-point";

  @MockitoBean
  private StopPointBulkImportService stopPointBulkImportService;

  @Test
  void shouldTerminateStopPointViaApi() throws Exception {
    //given
    BulkImportUpdateContainer<SloidTerminateCsvModel> container = BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
        .lineNumber(1)
        .object(SloidTerminateCsvModel.builder().sloid("sloid").validTo(LocalDate.of(2025, 1, 1)).build())
        .build();

    //when & then
    mvc.perform(post(TERMINATE_STOP_POINT_PATH)
            .contentType(contentType)
            .content(mapper.writeValueAsString(List.of(container))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].lineNumber", is(1)));

    verify(stopPointBulkImportService).terminateStopPoint(ArgumentMatchers.any());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldNotTerminateStopPointViaApiUnauthorized() throws Exception {
    mvc.perform(post(TERMINATE_STOP_POINT_PATH)
            .contentType(contentType)
            .content(mapper.writeValueAsString(List.of())))
        .andExpect(status().isForbidden());
  }

}
