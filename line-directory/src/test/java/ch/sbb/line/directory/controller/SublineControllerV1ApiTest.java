package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.Test;

class SublineControllerV1ApiTest extends BaseControllerApiTest {

  @Test
  void shouldReturnNotFoundErrorResponseWhenNoFoundLines() throws Exception {
    //when
    mvc.perform(get("/v1/sublines/versions/123")
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

}
