package ch.sbb.atlas.user.administration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@EmbeddedKafka
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class UserAdministrationUnauthorizedTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void shouldGetRedactedDisplaynames() throws Exception {
    mvc.perform(get("/v1/users/u123456/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("*****"));
  }

  @Test
  void shouldGetRedactedUserInformation() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/v1/users/display-info?userIds=u236171"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldGetRedactedUsersViaSearchInAtlas() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/v1/search-in-atlas?searchQuery=u225336&applicationType=SEPODI "))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}