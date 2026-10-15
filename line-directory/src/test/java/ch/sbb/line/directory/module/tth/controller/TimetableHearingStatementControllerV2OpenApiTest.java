package ch.sbb.line.directory.module.tth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class TimetableHearingStatementControllerV2OpenApiTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void shouldPublishCreateStatementExternalAsDeprecated() throws Exception {
    // When / Then
    mvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.paths['/v2/timetable-hearing/statements/external'].post.deprecated").value(true));
  }

}
