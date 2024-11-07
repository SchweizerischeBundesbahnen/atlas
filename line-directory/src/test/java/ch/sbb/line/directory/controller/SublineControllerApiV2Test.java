package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class SublineControllerApiV2Test extends BaseControllerApiTest {

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSublineVersion() throws Exception {
    //given
    LineVersion lineVersion = LineTestData.lineVersionV2Builder().build();
    LineVersion savedLineVersion = lineVersionRepository.saveAndFlush(lineVersion);
    SublineVersion sublineVersion = SublineTestData.sublineVersionV2Builder()
        .mainlineSlnid(savedLineVersion.getSlnid())
        .build();
    sublineVersionRepository.saveAndFlush(sublineVersion);

    //when
    mvc.perform(get("/v2/sublines/versions/" + sublineVersion.getSlnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
