package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReferencePointVersionControllerApiTest extends BaseControllerApiTest {

  private final ReferencePointRepository referencePointRepository;

  @Autowired
  ReferencePointVersionControllerApiTest(ReferencePointRepository referencePointRepository){
    this.referencePointRepository = referencePointRepository;
  }

  @BeforeEach()
  void initDB() {
    referencePointRepository.save(ReferencePointTestData.getReferencePointVersion());
  }

  @AfterEach
  void tearDown() {
    referencePointRepository.deleteAll();
  }

  @Test
  void shouldGetStopPlacesVersion() throws Exception {
    mvc.perform(get("/v1/reference-points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
