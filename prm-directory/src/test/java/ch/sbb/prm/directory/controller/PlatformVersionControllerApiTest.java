package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.repository.PlatformRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PlatformVersionControllerApiTest extends BaseControllerApiTest {

  private final PlatformRepository platformRepository;

  @Autowired
  PlatformVersionControllerApiTest(PlatformRepository platformRepository){
    this.platformRepository = platformRepository;
  }

  @BeforeEach()
  void initDB() {
    platformRepository.save(PlatformTestData.getPlatformVersion());
  }

  @AfterEach
  void tearDown() {
    platformRepository.deleteAll();
  }

  @Test
  void shouldGetPlatformsVersion() throws Exception {
    mvc.perform(get("/v1/platforms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
