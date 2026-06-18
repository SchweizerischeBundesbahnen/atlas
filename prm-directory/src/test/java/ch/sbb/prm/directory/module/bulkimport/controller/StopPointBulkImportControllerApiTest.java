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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class StopPointBulkImportControllerApiTest extends BaseControllerApiTest {

  private static final String TERMINATE_STOP_POINT_PATH = "/internal/stop-point/bulk-import/terminate-stop-point";

  @MockitoBean
  private StopPointBulkImportService stopPointBulkImportService;

  @Nested
  @DisplayName("TERMINATE STOP POINT - POST /internal/stop-point/bulk-import/terminate-stop-point")
  class TerminateStopPoint {

    private ResultActions terminateStopPoint() throws Exception {
      BulkImportUpdateContainer<SloidTerminateCsvModel> container = BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
          .lineNumber(1)
          .object(SloidTerminateCsvModel.builder().sloid("sloid").validTo(LocalDate.of(2025, 1, 1)).build())
          .build();

      return mvc.perform(post(TERMINATE_STOP_POINT_PATH)
          .contentType(contentType)
          .content(mapper.writeValueAsString(List.of(container))));
    }

    @Test
    void shouldTerminateStopPointViaApi() throws Exception {
      terminateStopPoint().andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].lineNumber", is(1)));

      verify(stopPointBulkImportService).terminateStopPoint(ArgumentMatchers.any());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotTerminateStopPointViaApiUnauthorized() throws Exception {
      terminateStopPoint().andExpect(status().isForbidden());
    }

  }

}
