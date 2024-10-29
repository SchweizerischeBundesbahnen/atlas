package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.Test;

class SublineControllerApiV2Test extends BaseControllerApiTest {

  @Test
  void shouldGetSubjineVersion() throws Exception {
    //when
    mvc.perform(get("/v2/sublines/versions/ch:1:slnid:1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
