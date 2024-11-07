package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class LineControllerApiV2Test extends BaseControllerApiTest {

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetLineVersion() throws Exception {
    //given
    LineVersion lineVersion = LineTestData.lineVersionV2Builder().build();
    lineVersionRepository.saveAndFlush(lineVersion);

    //when
    mvc.perform(get("/v2/lines/versions/" + lineVersion.getSlnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
