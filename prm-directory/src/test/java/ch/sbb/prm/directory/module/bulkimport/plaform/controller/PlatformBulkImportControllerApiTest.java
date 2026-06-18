package ch.sbb.prm.directory.module.bulkimport.plaform.controller;

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
import ch.sbb.prm.directory.module.bulkimport.service.PlatformBulkImportService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class PlatformBulkImportControllerApiTest extends BaseControllerApiTest {

  private static final String TERMINATE_PLATFORM_PATH = "/internal/platform/bulk-import/terminate-platform";

  @MockitoBean
  private PlatformBulkImportService platformBulkImportService;

  @Nested
  @DisplayName("TERMINATE PLATFORM - POST /internal/platform/bulk-import/terminate-platform")
  class TerminatePlatform {

    private ResultActions terminatePlatform() throws Exception {
      BulkImportUpdateContainer<SloidTerminateCsvModel> container = BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
          .lineNumber(1)
          .object(SloidTerminateCsvModel.builder().sloid("sloid").validTo(LocalDate.of(2025, 1, 1)).build())
          .build();

      return mvc.perform(post(TERMINATE_PLATFORM_PATH)
          .contentType(contentType)
          .content(mapper.writeValueAsString(List.of(container))));
    }

    @Test
    void shouldTerminatePlatformViaApi() throws Exception {
      terminatePlatform().andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].lineNumber", is(1)));

      verify(platformBulkImportService).terminatePlatform(ArgumentMatchers.any());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotTerminatePlatformViaApiUnauthorized() throws Exception {
      terminatePlatform().andExpect(status().isForbidden());
    }
  }
}
