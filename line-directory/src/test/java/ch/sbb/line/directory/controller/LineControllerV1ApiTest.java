package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.repository.LineVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@MockitoBean(types = SharedBusinessOrganisationService.class)
class LineControllerV1ApiTest extends BaseControllerApiTest {

  private final LineControllerV2 lineControllerV2;
  private final LineVersionRepository lineVersionRepository;

  @Autowired
  LineControllerV1ApiTest(
      LineControllerV2 lineControllerV2,
      LineVersionRepository lineVersionRepository
  ) {
    this.lineControllerV2 = lineControllerV2;
    this.lineVersionRepository = lineVersionRepository;
  }

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetLineOverview() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineControllerV2.createLineVersionV2(lineVersionModel);

    //when
    mvc.perform(get("/v1/lines")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "swissLineNumber,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldReturnNotFoundErrorResponseWhenNoFoundLines() throws Exception {
    //when
    mvc.perform(get("/v1/lines/versions/123")
            .contentType(contentType))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with slnid 123 not found")))
        .andExpect(jsonPath("$.details[0].field", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("slnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
  }

  @Test
  void shouldReturnBadRequestWhenPageSizeExceeded() throws Exception {
    mvc.perform(get("/v1/lines?size=5000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("The page size is limited to 2000")));
  }
}
