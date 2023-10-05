package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.controller.model.CreateStopPlaceVersionModel;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StopPlaceVersionControllerApiTest extends BaseControllerApiTest {

  private final StopPlaceRepository stopPlaceRepository;

  @Autowired
  StopPlaceVersionControllerApiTest(StopPlaceRepository stopPlaceRepository) {
    this.stopPlaceRepository = stopPlaceRepository;
  }

  @BeforeEach()
  void initDB() {
    stopPlaceRepository.save(StopPlaceTestData.getStopPlaceVersion());
  }

  @AfterEach
  void tearDown() {
    stopPlaceRepository.deleteAll();
  }

  @Test
  void shouldGetStopPlacesVersion() throws Exception {
    mvc.perform(get("/v1/stop-places"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateStopPlace() throws Exception {
    //given
    CreateStopPlaceVersionModel stopPlaceCreateVersionModel = StopPlaceTestData.getStopPlaceCreateVersionModel();
    //when && then
    mvc.perform(post("/v1/stop-places").contentType(contentType)
            .content(mapper.writeValueAsString(stopPlaceCreateVersionModel)))
        .andDo(print())
        .andExpect(status().isCreated());

  }

}
