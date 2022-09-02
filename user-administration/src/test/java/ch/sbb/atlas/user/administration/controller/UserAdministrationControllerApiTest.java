package ch.sbb.atlas.user.administration.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.enums.ApplicationRole;
import ch.sbb.atlas.user.administration.enums.ApplicationType;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class UserAdministrationControllerApiTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @AfterEach
  void cleanup() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldGetUsers() throws Exception {
    userPermissionRepository.saveAll(List.of(
        UserPermission.builder()
                      .role(ApplicationRole.ADMIN)
                      .application(ApplicationType.TTFN)
                      .sbbUserId("u236171").build(),
        UserPermission.builder()
                      .role(ApplicationRole.ADMIN)
                      .application(ApplicationType.TTFN)
                      .sbbUserId("e678574").build(),
        UserPermission.builder()
                      .role(ApplicationRole.ADMIN)
                      .application(ApplicationType.TTFN)
                      .sbbUserId("u236171").build()
    ));

    mvc.perform(get("/v1/users")
           .queryParam("page", "0")
           .queryParam("size", "5"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.totalCount").value(2))
       .andExpect(jsonPath("$.objects", hasSize(2)))
       .andExpect(jsonPath("$.objects", hasItems("e678574", "u236171")));
  }

  @Test
  void shouldGetUserPermissions() throws Exception {
    userPermissionRepository.saveAll(List.of(
        UserPermission.builder()
                      .role(ApplicationRole.ADMIN)
                      .application(ApplicationType.TTFN)
                      .sbbUserId("u236171").build(),
        UserPermission.builder()
                      .role(ApplicationRole.WRITER)
                      .application(ApplicationType.LIDI)
                      .sbbUserId("U236171").build(),
        UserPermission.builder()
                      .role(ApplicationRole.ADMIN)
                      .application(ApplicationType.TTFN)
                      .sbbUserId("e678574").build()
    ));

    mvc.perform(get("/v1/users/u236171/permissions"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$", hasSize(2)));
  }

}
