package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc
class UserAdministrationSecurityConfigTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
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

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldAllowDisplayNameQueryForUnauthorizedInternalRoleAndMaskResponse() throws Exception {
    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("*****"));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldAllowDisplayNameQueryForAuthorizedInternalRoleAndNotMaskResponse() throws Exception {
    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value(not("*****")));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.NONE)
  void shouldNotAllowDisplayNameQueryForOthersWithNoRoles() throws Exception {
    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldNotAllowSearchToUnauthorizedInternal() throws Exception {
    mvc.perform(get("/v1/search").param("searchQuery", "testQuery"))
        .andExpect(status().isForbidden());
  }
}
