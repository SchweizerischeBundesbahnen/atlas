package ch.sbb.atlas.user.administration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.TestcontainersConfiguration;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@EmbeddedKafka
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class UserAdministrationUnauthorizedTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldGetRedactedDisplaynames() throws Exception {
    mvc.perform(get("/v1/users/u236171/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("*****"));
  }

  @Test
  void shouldGetRedactedUserInformation() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/v1/users/display-info?userIds=u236171"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].displayName").value("*****"));
  }

  @Test
  void shouldGetRedactedUsersViaSearchInAtlas() throws Exception {
    userPermissionRepository.save(UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .sbbUserId("u225336").build());

    mvc.perform(MockMvcRequestBuilders.get("/v1/search-in-atlas?searchQuery=u225336&applicationType=SEPODI"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].displayName").value("*****"));
  }
}