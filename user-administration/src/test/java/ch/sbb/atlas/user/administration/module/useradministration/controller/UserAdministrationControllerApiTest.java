package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.user.administration.ManualMailOverrideModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel.Fields;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.module.clientcredential.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.module.clientcredential.repository.ClientCredentialPermissionRepository;
import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMailOverride;
import ch.sbb.atlas.user.administration.module.manualmail.repository.UserManualMailOverrideRepository;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class UserAdministrationControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private GraphServiceClient graphClient;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @Autowired
  private ClientCredentialPermissionRepository clientCredentialPermissionRepository;

  @Autowired
  private UserManualMailOverrideRepository userManualMailOverrideRepository;

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
    return buildGraphApiUserResult(List.of(sbbuid));
  }

  private static UsersRequestBuilder buildGraphApiUserResult(List<String> sbbuids) {
    UsersRequestBuilder usersRequestBuilderMock = mock(UsersRequestBuilder.class);
    UserCollectionResponse userCollectionResponseMock = mock(UserCollectionResponse.class);
    List<User> graphUsers = sbbuids.stream().map(sbbuid -> {
      User graphUser = new User();
      graphUser.setDisplayName("Lastname Firstname");
      graphUser.setOnPremisesSamAccountName(sbbuid);
      graphUser.setSurname("Lastname");
      graphUser.setGivenName("Firstname");
      graphUser.setMail(sbbuid + "@sbb.ch");
      graphUser.setAccountEnabled(true);
      return graphUser;
    }).toList();
    when(userCollectionResponseMock.getValue()).thenReturn(graphUsers);
    when(usersRequestBuilderMock.get(any())).thenReturn(userCollectionResponseMock);
    return usersRequestBuilderMock;
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
    clientCredentialPermissionRepository.deleteAll();
    userManualMailOverrideRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET v1/users/current")
  class GetCurrentUser {

    @Test
    void shouldGetCurrentUserInformationAsAdmin() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/v1/users/current")).andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetCurrentUserInformationAsStandardUser() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/v1/users/current")).andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetCurrentUserInformationUnauthorized() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/v1/users/current")).andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("GET v1/users/{userId}/displayname")
  class GetUserDisplayName {

    @Test
    void shouldGetUserDisplayNameForExistingClientCredential() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/client-id/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value("PostAuto"));
    }

    @Test
    void shouldGetUserDisplayNameNotExistingClientCredential() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/ATLAS_SYSTEM_USER/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").doesNotExist());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldAllowDisplayNameQueryForUnauthorizedAndMaskResponse() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/u123456/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value("*****"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAllowDisplayNameQueryForStandardUserAndNotMaskResponse() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/u123456/displayname"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.displayName").value("Lastname Firstname"));
    }
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

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetUsersAsUnauthorized() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET v1/users/emails")
  class GetUserEmails {

    private static final String SBOID = "ch:1:sboid:20009";

    private void saveUserWithBoRestriction(String sbbUserId, ApplicationRole role) {
      UserPermission userPermission = UserPermission.builder()
          .sbbUserId(sbbUserId)
          .role(role)
          .application(ApplicationType.SEPODI)
          .build();
      userPermission.getPermissionRestrictions().add(PermissionRestriction.builder()
          .userPermission(userPermission)
          .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
          .restriction(SBOID)
          .build());
      userPermissionRepository.saveAndFlush(userPermission);
    }

    @Test
    void shouldReturnEffectiveMailsOfAllFilteredUsersAcrossAllPages() throws Exception {
      // given: 25 writers matching the BO filter - more than one default table page and more
      // than one Graph API resolve batch (RESOLVE_CHUNK_SIZE = 20)
      List<String> sbbUserIds = new ArrayList<>();
      for (int i = 0; i < 25; i++) {
        String sbbUserId = "u2%04d".formatted(i);
        sbbUserIds.add(sbbUserId);
        saveUserWithBoRestriction(sbbUserId, ApplicationRole.WRITER);
      }
      when(graphClient.users()).thenReturn(buildGraphApiUserResult(sbbUserIds));

      // when & then
      mvc.perform(get("/v1/users/emails")
              .queryParam("permissionRestrictions", SBOID)
              .queryParam("type", "BUSINESS_ORGANISATION")
              .queryParam("applicationTypes", "SEPODI"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(25)))
          .andExpect(jsonPath("$", hasItem("u20000@sbb.ch")))
          .andExpect(jsonPath("$", hasItem("u20024@sbb.ch")));
    }

    @Test
    void shouldPreferManualMailOverrideOverAzureMail() throws Exception {
      // given
      saveUserWithBoRestriction("u123456", ApplicationRole.WRITER);
      userManualMailRepository.save(UserManualMailOverride.builder().sbbUserId("u123456").mail("manual@sbb.ch").build());

      // when & then
      mvc.perform(get("/v1/users/emails")
              .queryParam("permissionRestrictions", SBOID)
              .queryParam("type", "BUSINESS_ORGANISATION")
              .queryParam("applicationTypes", "SEPODI"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0]").value("manual@sbb.ch"));
    }

    @Test
    void shouldSkipUsersWithoutResolvableMail() throws Exception {
      // given: "u-ghost" has a matching permission but Graph does not return it (default mock
      // only resolves "u123456"), so it comes back as accountStatus=DELETED without a mail
      saveUserWithBoRestriction("u123456", ApplicationRole.WRITER);
      saveUserWithBoRestriction("u-ghost", ApplicationRole.WRITER);

      // when & then
      mvc.perform(get("/v1/users/emails")
              .queryParam("permissionRestrictions", SBOID)
              .queryParam("type", "BUSINESS_ORGANISATION")
              .queryParam("applicationTypes", "SEPODI"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0]").value("u123456@sbb.ch"));
    }

    @Test
    void shouldReturnDistinctMails() throws Exception {
      // given: two sbbUserIds sharing the same manually maintained mail address
      saveUserWithBoRestriction("u123456", ApplicationRole.WRITER);
      saveUserWithBoRestriction("u654321", ApplicationRole.WRITER);
      when(graphClient.users()).thenReturn(buildGraphApiUserResult(List.of("u123456", "u654321")));
      userManualMailRepository.save(UserManualMailOverride.builder().sbbUserId("u123456").mail("shared@sbb.ch").build());
      userManualMailRepository.save(UserManualMailOverride.builder().sbbUserId("u654321").mail("shared@sbb.ch").build());

      // when & then
      mvc.perform(get("/v1/users/emails")
              .queryParam("permissionRestrictions", SBOID)
              .queryParam("type", "BUSINESS_ORGANISATION")
              .queryParam("applicationTypes", "SEPODI"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0]").value("shared@sbb.ch"));
    }

    @Test
    void shouldRejectRequestWhenRestrictionsGivenWithoutType() throws Exception {
      mvc.perform(get("/v1/users/emails").queryParam("permissionRestrictions", SBOID))
          .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotBeShadowedByUserIdPathVariable() throws Exception {
      mvc.perform(get("/v1/users/emails"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetUserEmailsAsUnauthorized() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/emails"))
          .andExpect(status().isForbidden());
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

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetUserAsUnauthorized() throws Exception {
      mvc.perform(RestDocumentationRequestBuilders.get("/v1/users/u123456"))
          .andExpect(status().isForbidden());
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

    @Test
    void shouldFindUserByManualMail() throws Exception {
      // given
      userManualMailOverrideRepository.save(UserManualMailOverride.builder().sbbUserId("u123456").mail("manual@sbb.ch").build());

      // when & then
      mvc.perform(get("/v1/users/mail?mail=manual@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.mail").value("manual@sbb.ch"))
          .andExpect(jsonPath("$.originalMail").value("u123456@sbb.ch"));
    }

    @Test
    void shouldFindUserByAzureMailWhenNoManualMailExists() throws Exception {
      // when & then
      mvc.perform(get("/v1/users/mail?mail=u123456@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.mail").value("u123456@sbb.ch"))
          .andExpect(jsonPath("$.originalMail").value("u123456@sbb.ch"));
    }

    @Test
    void shouldPreferManualMailMatchOverAzureMatch() throws Exception {
      // given: "u123456@yb.com" is u123456's real Azure mail (see shouldFindUserByAzureMailWhenNoManualMailExists),
      // but e123456 has manually claimed the very same address as an override.
      userManualMailOverrideRepository.save(UserManualMailOverride.builder().sbbUserId("e123456").mail("u123456@yb.com").build());

      // when & then: the manual-mail-override owner (e123456) wins, not the Azure owner (u123456)
      mvc.perform(get("/v1/users/mail?mail=u123456@yb.com"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("e123456"))
          .andExpect(jsonPath("$.mail").value("u123456@yb.com"));
    }
  }

  @Nested
  @DisplayName("PUT/DELETE v1/users/{userId}/mail")
  class ManualMail {

    @Test
    void shouldSetManualMailForUser() throws Exception {
      // when & then
      mvc.perform(put("/v1/users/u123456/mail").contentType(contentType)
              .content(mapper.writeValueAsString(new ManualMailOverrideModel("manual@sbb.ch"))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.mail").value("manual@sbb.ch"))
          .andExpect(jsonPath("$.originalMail").value("u123456@sbb.ch"));
    }

    @Test
    void shouldDeleteManualMailForUser() throws Exception {
      // given
      userManualMailOverrideRepository.save(UserManualMailOverride.builder().sbbUserId("u123456").mail("manual@sbb.ch").build());

      // when & then
      mvc.perform(delete("/v1/users/u123456/mail"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.mail").value("u123456@sbb.ch"))
          .andExpect(jsonPath("$.originalMail").value("u123456@sbb.ch"));
    }

    @Test
    void shouldRejectInvalidManualMailFormat() throws Exception {
      mvc.perform(put("/v1/users/u123456/mail").contentType(contentType)
              .content(mapper.writeValueAsString(new ManualMailOverrideModel("not-a-mail"))))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldForbidManualMailUpdateForNonAdmin() throws Exception {
      mvc.perform(put("/v1/users/u123456/mail").contentType(contentType)
              .content(mapper.writeValueAsString(new ManualMailOverrideModel("manual@sbb.ch"))))
          .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnManualMailInUserOverview() throws Exception {
      // given
      userManualMailOverrideRepository.save(UserManualMailOverride.builder().sbbUserId("u123456").mail("manual@sbb.ch").build());

      // when & then
      mvc.perform(get("/v1/users").queryParam("page", "0").queryParam("size", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u123456')].mail").value("manual@sbb.ch"))
          .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u123456')].originalMail").value("u123456@sbb.ch"));
    }

    @Test
    void shouldReturnManualMailForCurrentUser() throws Exception {
      // given: the default mocked JWT identifies the current user as "e123456" (see IntegrationTest)
      userManualMailOverrideRepository.save(UserManualMailOverride.builder().sbbUserId("e123456").mail("manual@sbb.ch").build());

      // when & then
      mvc.perform(get("/v1/users/current"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mail").value("manual@sbb.ch"));
    }
  }

  @Nested
  @DisplayName("POST v1/users")
  class CreateUserPermission {

    @Test
    void shouldCreateUserPermissionWithAllReaderPermissions() throws Exception {
      createUserPermission()
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$." + Fields.sbbUserId).value("u234565"))
          .andExpect(jsonPath("$." + Fields.mail).value("u234565@sbb.ch"));
    }

    private ResultActions createUserPermission() throws Exception {
      UsersRequestBuilder users = buildGraphApiUserResult("u234565");
      when(graphClient.users()).thenReturn(users);

      UserPermissionCreateModel model = UserPermissionCreateModel
          .builder()
          .sbbUserId("u234565")
          .build();

      // when & then
      return mvc.perform(post("/v1/users")
          .content(mapper.writeValueAsString(model))
          .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotCreateUserPermissionAsStandardUser() throws Exception {
      createUserPermission().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCreateUserPermissionAsUnauthorized() throws Exception {
      createUserPermission().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PUT v1/users/{userId}/{application}")
  class UpdateUserPermissions {

    @Test
    void shouldUpdateUserPermission() throws Exception {
      updateUserPermission()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.sbbUserId").value("u123456"))
          .andExpect(jsonPath("$.lastName").value("Lastname"))
          .andExpect(jsonPath("$.permissions").value(hasSize(2)));
    }

    private ResultActions updateUserPermission() throws Exception {
      // given
      PermissionModel permission = PermissionModel.builder()
          .application(ApplicationType.TTFN)
          .role(ApplicationRole.WRITER)
          .permissionRestrictions(new ArrayList<>(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:10009"))))
          .build();

      // when & then
      return mvc.perform(put("/v1/users/u123456/TTFN").contentType(contentType)
          .content(mapper.writeValueAsString(permission)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotUpdateUserPermissionAsStandardUser() throws Exception {
      updateUserPermission().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateUserPermissionAsUnauthorized() throws Exception {
      updateUserPermission().andExpect(status().isForbidden());
    }
  }
}
