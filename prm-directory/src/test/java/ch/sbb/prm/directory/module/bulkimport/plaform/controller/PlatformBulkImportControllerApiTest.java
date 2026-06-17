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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class PlatformBulkImportControllerApiTest extends BaseControllerApiTest {

  private static final String TERMINATE_PLATFORM_PATH = "/internal/platform/bulk-import/terminate-platform";

  @MockitoBean
  private PlatformBulkImportService platformBulkImportService;

  @Test
  void shouldPlatformStopPointViaApi() throws Exception {
    //given
    BulkImportUpdateContainer<SloidTerminateCsvModel> container = BulkImportUpdateContainer.<SloidTerminateCsvModel>builder()
        .lineNumber(1)
        .object(SloidTerminateCsvModel.builder().sloid("sloid").validTo(LocalDate.of(2025, 1, 1)).build())
        .build();

    //when & then
    mvc.perform(post(TERMINATE_PLATFORM_PATH)
            .contentType(contentType)
            .content(mapper.writeValueAsString(List.of(container))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].lineNumber", is(1)));

    verify(platformBulkImportService).terminatePlatform(ArgumentMatchers.any());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldNotTerminatePlatformViaApiUnauthorized() throws Exception {
    mvc.perform(post(TERMINATE_PLATFORM_PATH)
            .contentType(contentType)
            .content(mapper.writeValueAsString(List.of())))
        .andExpect(status().isForbidden());
  }

}
