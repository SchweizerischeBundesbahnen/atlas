package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.model.controller.TestcontainersConfiguration;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication.MockUnauthorizedJwtAuthenticationFactory;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
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
  void shouldAllowDisplayNameQueryForUnauthorizedInternalRoleAndMaskResponse() throws Exception {
    Authentication authentication = new JwtAuthenticationToken(MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456"),
        AuthorityUtils.createAuthorityList(Role.AUTHORITY_UNAUTHORIZED, Role.AUTHORITY_INTERNAL));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("*****"));
  }

  @Test
  void shouldAllowDisplayNameQueryForAuthorizedInternalRoleAndNotMaskResponse() throws Exception {
    Jwt jwt = MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456", List.of(Role.ATLAS_INTERNAL));
    Authentication authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(Role.AUTHORITY_INTERNAL));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value(not("*****")));
  }

  @Test
  void shouldNotAllowDisplayNameQueryForOthersWithNoRoles() throws Exception {
    Jwt jwt = MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456", List.of());
    Authentication authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldNotAllowSearchToUnauthorizedInternal() throws Exception {
    Authentication authentication = new JwtAuthenticationToken(MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456"),
        AuthorityUtils.createAuthorityList(Role.AUTHORITY_UNAUTHORIZED, Role.AUTHORITY_INTERNAL));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/search").param("searchQuery", "testQuery"))
        .andExpect(status().isForbidden());
  }
}