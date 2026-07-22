package ch.sbb.line.directory.module.bulkimport.line.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.line.directory.module.bulkimport.line.service.LineBulkImportService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class LineBulkImportControllerInternalApiTest extends BaseControllerApiTest {

  private static final String CREATE_LINE_PATH = "/internal/line/bulk-import/create";

  @MockitoBean
  private LineBulkImportService lineBulkImportService;

  @Nested
  @DisplayName("CREATE LINE - POST /internal/line/bulk-import/create")
  class CreateLine {

    private ResultActions createLine() throws Exception {
      BulkImportUpdateContainer<LineCreateCsvModel> container = BulkImportUpdateContainer.<LineCreateCsvModel>builder()
          .lineNumber(1)
          .object(LineCreateCsvModel.builder()
              .linienId("320")
              .validFrom(LocalDate.of(2021, 4, 1))
              .validTo(LocalDate.of(2099, 12, 31))
              .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
              .number("BEX")
              .swissLineNumber("b0.BEX0")
              .lineType(LineType.ORDERLY)
              .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
              .offerCategory(OfferCategory.IR)
              .shortNumber("EX")
              .longName("Bernina Express")
              .businessOrganisation("ch:1:sboid:100053")
              .comment("Bernina Express / Konzessionsrecht ist nur für den schweizerischen Linienabschnitt gültig")
              .build())
          .build();

      return mvc.perform(post(CREATE_LINE_PATH)
          .contentType(contentType)
          .content(mapper.writeValueAsString(List.of(container))));
    }

    @Test
    void shouldCreateLineViaApi() throws Exception {
      createLine().andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].lineNumber", is(1)));

      verify(lineBulkImportService).createLine(ArgumentMatchers.any());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCreateLineViaApiUnauthorized() throws Exception {
      createLine().andExpect(status().isForbidden());
    }
  }

}
