package ch.sbb.atlas.user.administration.module.userinformation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMailOverride;
import ch.sbb.atlas.user.administration.module.manualmail.repository.UserManualMailOverrideRepository;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import com.microsoft.graph.users.UsersRequestBuilder.GetRequestConfiguration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class UserInformationApiControllerTest extends BaseControllerApiTest {

  @MockitoBean
  private GraphServiceClient graphClient;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @Autowired
  private UserManualMailOverrideRepository userManualMailOverrideRepository;

  @BeforeEach
  void setUp() {
    UsersRequestBuilder users = buildGraphApiUserResult();
    when(graphClient.users()).thenReturn(users);

    userPermissionRepository.save(UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .sbbUserId("u123456").build());
  }

  /**
   * Answers like Azure/Graph: the user is only found by values known to Azure, never by a mail that exists only as manual
   * override in atlas.
   */
  private static UsersRequestBuilder buildGraphApiUserResult() {
    UsersRequestBuilder usersRequestBuilderMock = mock(UsersRequestBuilder.class);
    User graphUser = new User();
    graphUser.setDisplayName("Lastname Firstname");
    graphUser.setOnPremisesSamAccountName("u123456");
    graphUser.setSurname("Lastname");
    graphUser.setGivenName("Firstname");
    graphUser.setMail("u123456@sbb.ch");
    graphUser.setAccountEnabled(true);

    UserCollectionResponse foundResponse = mock(UserCollectionResponse.class);
    when(foundResponse.getValue()).thenReturn(List.of(graphUser));
    UserCollectionResponse emptyResponse = mock(UserCollectionResponse.class);
    when(emptyResponse.getValue()).thenReturn(List.of());

    when(usersRequestBuilderMock.get(any())).thenAnswer(invocation -> {
      GetRequestConfiguration requestConfiguration = mock(UsersRequestBuilder.class).new GetRequestConfiguration();
      invocation.<Consumer<GetRequestConfiguration>>getArgument(0).accept(requestConfiguration);
      String search = Objects.toString(requestConfiguration.queryParameters.search, "");
      String filter = Objects.toString(requestConfiguration.queryParameters.filter, "");
      boolean knownToAzure = search.contains("u123456") || filter.contains("'u123456'");
      return knownToAzure ? foundResponse : emptyResponse;
    });
    return usersRequestBuilderMock;
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
    userManualMailOverrideRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET /v1/search")
  class SearchUsers {

    @Test
    void shouldSearchUserInAD() throws Exception {
      mvc.perform(get("/v1/search")
              .param("searchQuery", "u123456"))
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotAllowSearchToUnauthorized() throws Exception {
      mvc.perform(get("/v1/search").param("searchQuery", "testQuery"))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotAllowSearchToStandardUser() throws Exception {
      mvc.perform(get("/v1/search").param("searchQuery", "testQuery")).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /v1/search-in-atlas")
  class SearchUsersInAtlas {

    @Test
    void shouldSearchUserInAtlas() throws Exception {
      mvc.perform(get("/v1/search-in-atlas")
              .param("searchQuery", "u123456")
              .param("applicationType", "SEPODI"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].sbbUserId").value("u123456"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetUsersViaSearchInAtlasAsUnauthorized() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/v1/search-in-atlas?searchQuery=u123456&applicationType=SEPODI"))
          .andExpect(status().isForbidden());
    }

    /**
     * BulkImport by writer with special bulk import rights
     */
    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetUsersViaSearchInAtlasAsStandardUser() throws Exception {
      mvc.perform(MockMvcRequestBuilders.get("/v1/search-in-atlas?searchQuery=u123456&applicationType=SEPODI"))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("GET /v1/search-bo-dossier-answering-users")
  class SearchBoDossierAnsweringUsers {

    @Test
    void shouldSearchBoUsers() throws Exception {
      givenBoDossierAnsweringPermission("u123456");

      mvc.perform(get("/v1/search-bo-dossier-answering-users")
              .param("searchQuery", "u123456@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    void shouldFindBoUserByManuallyOverriddenMailUnknownToAzure() throws Exception {
      // Given
      givenBoDossierAnsweringPermission("u123456");
      userManualMailOverrideRepository.save(
          UserManualMailOverride.builder().sbbUserId("u123456").mail("manual-bo@sbb.ch").build());

      // When / Then
      mvc.perform(get("/v1/search-bo-dossier-answering-users")
              .param("searchQuery", "manual-bo@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1))
          .andExpect(jsonPath("$[0].sbbUserId").value("u123456"))
          .andExpect(jsonPath("$[0].mail").value("manual-bo@sbb.ch"))
          .andExpect(jsonPath("$[0].originalMail").value("u123456@sbb.ch"));
    }

    @Test
    void shouldNotFindBoUserByManuallyOverriddenMailOfAnotherUser() throws Exception {
      // Given
      givenBoDossierAnsweringPermission("u123456");
      userManualMailOverrideRepository.save(
          UserManualMailOverride.builder().sbbUserId("u999999").mail("other-bo@sbb.ch").build());

      // When / Then
      mvc.perform(get("/v1/search-bo-dossier-answering-users")
              .param("searchQuery", "other-bo@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(0));
    }

    private void givenBoDossierAnsweringPermission(String sbbUserId) {
      UserPermission userPermission = UserPermission.builder()
          .role(ApplicationRole.READER)
          .application(ApplicationType.TIMETABLE_HEARING)
          .sbbUserId(sbbUserId)
          .build();
      userPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
          .userPermission(userPermission)
          .type(PermissionRestrictionType.TRANSPORT_COMPANY_DOSSIER_ANSWER)
          .restriction("true")
          .build()));
      userPermissionRepository.save(userPermission);
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotSearchBoUsersAsUnauthorized() throws Exception {
      mvc.perform(get("/v1/search-bo-dossier-answering-users").param("searchQuery", "u123456@sbb.ch"))
          .andExpect(status().isForbidden());
    }

    /**
     * Dossier canton writer looks for bo contact
     */
    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetUsersViaSearchInAtlasAsStandardUser() throws Exception {
      mvc.perform(get("/v1/search-bo-dossier-answering-users").param("searchQuery", "u123456@sbb.ch")).andExpect(status().isOk());
    }
  }
}