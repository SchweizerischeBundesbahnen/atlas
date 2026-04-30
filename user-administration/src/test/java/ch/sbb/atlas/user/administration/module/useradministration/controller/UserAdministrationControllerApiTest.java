package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel.Fields;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.module.clientcredential.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.module.clientcredential.repository.ClientCredentialPermissionRepository;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class UserAdministrationControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private GraphServiceClient graphClient;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @Autowired
  private ClientCredentialPermissionRepository clientCredentialPermissionRepository;

  @BeforeEach
  void setUp() {
    UsersRequestBuilder users = buildGraphApiUserResult("u123456");
    when(graphClient.users()).thenReturn(users);

    userPermissionRepository.save(UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .sbbUserId("u123456").build());

    clientCredentialPermissionRepository.save(ClientCredentialPermission.builder()
        .application(ApplicationType.PRM)
        .role(ApplicationRole.WRITER)
        .clientCredentialId("client-id")
        .alias("PostAuto")
        .build());
  }

  private static UsersRequestBuilder buildGraphApiUserResult(String sbbuid) {
    UsersRequestBuilder usersRequestBuilderMock = Mockito.mock(UsersRequestBuilder.class);
    UserCollectionResponse userCollectionResponseMock = Mockito.mock(UserCollectionResponse.class);
    User graphUser = new User();
    graphUser.setDisplayName("Lastname Firstname");
    graphUser.setOnPremisesSamAccountName(sbbuid);
    graphUser.setSurname("Lastname");
    graphUser.setGivenName("Firstname");
    graphUser.setMail(sbbuid + "@sbb.ch");
    graphUser.setAccountEnabled(true);
    when(userCollectionResponseMock.getValue()).thenReturn(List.of(graphUser));
    when(usersRequestBuilderMock.get(any())).thenReturn(userCollectionResponseMock);
    return usersRequestBuilderMock;
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
    clientCredentialPermissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET v1/users")
  class GetUsersOverview {

    @Test
    void shouldGetUsers() throws Exception {
      // when & then
      mvc.perform(get("/v1/users")
              .queryParam("page", "0")
              .queryParam("size", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalCount").value(1))
          .andExpect(jsonPath("$.objects", hasSize(1)))
          .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u123456')].accountStatus").value("ACTIVE"))
          .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u123456')].permissions[0].role").value("SUPERVISOR"))
          .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u123456')].permissions[0].application").value("SEPODI"));
    }

    @Test
    void shouldGetUsersWithSboidsAndApplicationTypesFound() throws Exception {
      // when & then
      mvc.perform(get("/v1/users")
              .queryParam("page", "0")
              .queryParam("size", "10")
              .queryParam("applicationTypes", "SEPODI"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.objects", hasSize(1)))
          .andExpect(jsonPath("$.objects[0].sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.objects[0].permissions", hasSize(1)));
    }
  }

  @Nested
  @DisplayName("GET v1/users/{userId}")
  class GetUser {

    @Test
    void shouldGetUser() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/u123456"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.lastName").value("Lastname"))
          .andExpect(jsonPath("$.permissions").value(hasSize(1)))
          .andExpect(jsonPath("$.permissions[0].role").value("SUPERVISOR"))
          .andExpect(jsonPath("$.permissions[0].application").value("SEPODI"));
    }
  }

  @Nested
  @DisplayName("GET v1/users/mail?mail={mail}")
  class GetUserByMail {

    @Test
    void shouldGetUserByMail() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/mail?mail=u123456@yb.com"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.lastName").value("Lastname"))
          .andExpect(jsonPath("$.permissions").value(hasSize(1)))
          .andExpect(jsonPath("$.permissions[0].role").value("SUPERVISOR"))
          .andExpect(jsonPath("$.permissions[0].application").value("SEPODI"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAllowSearchByMailForWriter() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/mail").param("mail", "e527717@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value(not("*****")));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotDisplayInfoForUnauthorizedOnSearchByMail() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/mail").param("mail", "e527717@sbb.ch"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET v1/users/{userId}/displayname")
  class GetUserDisplayName {

    @Test
    void shouldGetUserDisplayNameExisting() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/client-id/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value("PostAuto"));
    }

    @Test
    void shouldGetUserDisplayNameNotExisting() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/ATLAS_SYSTEM_USER/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").doesNotExist());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldAllowDisplayNameQueryForUnauthorizedInternalRoleAndMaskResponse() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/u123456/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value("*****"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAllowDisplayNameQueryForAuthorizedInternalRoleAndNotMaskResponse() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/u123456/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value(not("*****")));
    }
  }

  @Nested
  @DisplayName("GET v1/users/current")
  class GetCurrentUser {

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetCurrentUserInformationUnauthorized() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/v1/users/current")).andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("POST v1/users")
  class CreateUserPermission {

    @Test
    void shouldCreateUserPermissionWithAllReaderPermissions() throws Exception {
      UsersRequestBuilder users = buildGraphApiUserResult("u234565");
      when(graphClient.users()).thenReturn(users);

      UserPermissionCreateModel model = UserPermissionCreateModel
          .builder()
          .sbbUserId("u234565")
          .build();

      // when & then
      mvc.perform(post("/v1/users")
              .content(mapper.writeValueAsString(model)).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$." + Fields.sbbUserId).value("u234565"))
          .andExpect(jsonPath("$." + Fields.mail).value("u234565@sbb.ch"));
    }

  }

  @Nested
  @DisplayName("PUT v1/users/{userId}/{application}")
  class UpdateUserPermissions {

    @Test
    void shouldUpdateUserPermission() throws Exception {
      // given
      PermissionModel permission = PermissionModel.builder()
          .application(ApplicationType.TTFN)
          .role(ApplicationRole.WRITER)
          .permissionRestrictions(new ArrayList<>(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:10009"))))
          .build();

      // when & then
      mvc.perform(put("/v1/users/u123456/TTFN").contentType(contentType)
              .content(mapper.writeValueAsString(permission)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.lastName").value("Lastname"))
          .andExpect(jsonPath("$.permissions").value(hasSize(2)));
    }
  }
}
