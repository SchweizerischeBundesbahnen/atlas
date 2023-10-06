package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.repository.ToiletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ToiletVersionControllerApiTest extends BaseControllerApiTest {

  private final ToiletRepository toiletRepository;

  @Autowired
  ToiletVersionControllerApiTest(ToiletRepository toiletRepository){
    this.toiletRepository = toiletRepository;
  }

  @BeforeEach()
  void initDB() {
    toiletRepository.save(ToiletTestData.getToiletVersion());
  }

  @Test
  void shouldGetPlatformsVersion() throws Exception {
    mvc.perform(get("/v1/toilets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
