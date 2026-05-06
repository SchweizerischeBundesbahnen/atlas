package ch.sbb.atlas.user.administration.module.userinformation.controller;

import static org.mockito.ArgumentMatchers.any;
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
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class UserInformationApiControllerTest extends BaseControllerApiTest {

  @MockitoBean
  private GraphServiceClient graphClient;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @BeforeEach
  void setUp() {
    UsersRequestBuilder users = buildGraphApiUserResult();
    when(graphClient.users()).thenReturn(users);

    userPermissionRepository.save(UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .sbbUserId("u123456").build());
  }

  private static UsersRequestBuilder buildGraphApiUserResult() {
    UsersRequestBuilder usersRequestBuilderMock = Mockito.mock(UsersRequestBuilder.class);
    UserCollectionResponse userCollectionResponseMock = Mockito.mock(UserCollectionResponse.class);
    User graphUser = new User();
    graphUser.setDisplayName("Lastname Firstname");
    graphUser.setOnPremisesSamAccountName("u123456");
    graphUser.setSurname("Lastname");
    graphUser.setGivenName("Firstname");
    graphUser.setMail("u123456@sbb.ch");
    graphUser.setAccountEnabled(true);
    when(userCollectionResponseMock.getValue()).thenReturn(List.of(graphUser));
    when(usersRequestBuilderMock.get(any())).thenReturn(userCollectionResponseMock);
    return usersRequestBuilderMock;
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
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
  @DisplayName("GET /v1/search-in-atlas")
  class SearchBoDossierAnsweringUsers {

    @Test
    void shouldSearchBoUsers() throws Exception {
      UserPermission userPermission = UserPermission.builder()
          .role(ApplicationRole.READER)
          .application(ApplicationType.TIMETABLE_HEARING)
          .sbbUserId("u123456")
          .build();
      userPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
          .userPermission(userPermission)
          .type(PermissionRestrictionType.TRANSPORT_COMPANY_DOSSIER_ANSWER)
          .restriction("true")
          .build()));
      userPermissionRepository.save(userPermission);

      mvc.perform(get("/v1/search-bo-dossier-answering-users")
              .param("searchQuery", "u123456@sbb.ch"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(1));

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