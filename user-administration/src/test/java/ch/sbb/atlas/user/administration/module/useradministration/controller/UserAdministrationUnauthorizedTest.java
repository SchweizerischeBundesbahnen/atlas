package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.TestcontainersConfiguration;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class UserAdministrationUnauthorizedTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @MockitoBean
  private GraphServiceClient graphClient;

  @BeforeEach
  void setUp() {
    UsersRequestBuilder usersRequestBuilderMock = Mockito.mock(UsersRequestBuilder.class);
    UserCollectionResponse userCollectionResponseMock = Mockito.mock(UserCollectionResponse.class);
    User graphUser = new User();
    graphUser.setDisplayName("Lastname Firstname");
    graphUser.setOnPremisesSamAccountName("user1");
    Mockito.when(userCollectionResponseMock.getValue())
        .thenReturn(List.of(graphUser));
    Mockito.when(usersRequestBuilderMock.get(any()))
        .thenReturn(userCollectionResponseMock);
    Mockito.when(graphClient.users()).thenReturn(usersRequestBuilderMock);
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldGetRedactedDisplaynames() throws Exception {
    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("*****"));
  }

  @Test
  void shouldGetRedactedUserInformation() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/v1/users/display-info?userIds=user1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].displayName").value("*****"));
  }

  @Test
  void shouldGetRedactedUsersViaSearchInAtlas() throws Exception {
    userPermissionRepository.save(UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .sbbUserId("user1").build());

    mvc.perform(MockMvcRequestBuilders.get("/v1/search-in-atlas?searchQuery=user1&applicationType=SEPODI"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].displayName").value("*****"));
  }
}