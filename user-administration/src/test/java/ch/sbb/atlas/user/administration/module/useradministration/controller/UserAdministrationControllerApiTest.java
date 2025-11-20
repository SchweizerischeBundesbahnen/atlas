package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserModel.Fields;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.module.clientcredential.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.module.clientcredential.service.ClientCredentialAdministrationService;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.mapper.UserPermissionMapper;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserAdministrationService;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionDistributor;
import ch.sbb.atlas.user.administration.module.userinformation.service.GraphApiService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class UserAdministrationControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private UserAdministrationService userAdministrationService;

  @MockitoBean
  private GraphApiService graphApiService;

  @MockitoBean
  private ClientCredentialAdministrationService clientCredentialAdministrationService;

  @MockitoSpyBean
  private UserPermissionDistributor userPermissionDistributor;

  @Test
  void shouldGetUsers() throws Exception {
    // given
    List<String> pageContent = List.of("user1", "user2");
    Mockito.when(userAdministrationService.getUserPage(any(Pageable.class), isNull(), isNull(), isNull()))
        .thenReturn(new PageImpl<>(pageContent, Pageable.ofSize(5), 2));
    Mockito.when(graphApiService.resolveUsers(pageContent))
        .thenReturn(List.of(
            UserModel.builder()
                .sbbUserId("user1")
                .accountStatus(UserAccountStatus.DELETED)
                .build(),
            UserModel.builder()
                .sbbUserId("user2")
                .accountStatus(UserAccountStatus.ACTIVE)
                .lastName("lastName")
                .build()
        ));
    Mockito.when(userAdministrationService.getUserPermissions(anyString()))
        .thenReturn(List.of(UserPermission.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TTFN)
            .permissionRestrictions(
                Set.of(
                    PermissionRestriction.builder()
                        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .restriction("ch:1:sboid:1")
                        .build()
                )
            )
            .build()));

    // when & then
    mvc.perform(get("/v1/users")
            .queryParam("page", "0")
            .queryParam("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(2))
        .andExpect(jsonPath("$.objects", hasSize(2)))
        .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'user1')].accountStatus").value("DELETED"))
        .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'user1')].permissions[0].role").value("SUPERVISOR"))
        .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'user1')].permissions[0].application").value("TTFN"))
        .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'user2')].accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'user2')].permissions[*]").value(hasSize(1)))
        .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'user2')].lastName").value("lastName"));
  }

  @Test
  void shouldGetUser() throws Exception {
    // given
    Mockito.when(graphApiService.resolveUsers(List.of("user1")))
        .thenReturn(List.of(
            UserModel.builder()
                .sbbUserId("user1")
                .lastName("lastName")
                .build()
        ));
    Mockito.when(userAdministrationService.getUserPermissions("user1"))
        .thenReturn(List.of(UserPermission.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TTFN)
            .permissionRestrictions(
                Set.of(
                    PermissionRestriction.builder()
                        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .restriction("ch:1:sboid:1")
                        .build()
                )
            )
            .build()));

    // when & then
    mvc.perform(get("/v1/users/user1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sbbUserId").value("user1"))
        .andExpect(jsonPath("$.lastName").value("lastName"))
        .andExpect(jsonPath("$.permissions").value(hasSize(1)))
        .andExpect(jsonPath("$.permissions[0].role").value("SUPERVISOR"))
        .andExpect(jsonPath("$.permissions[0].application").value("TTFN"));
  }

  @Test
  void shouldCreateUserPermissionWithAllReaderPermissions() throws Exception {
    // given
    Mockito.when(graphApiService.resolveUsers(List.of("user1")))
        .thenReturn(List.of(
            UserModel.builder()
                .sbbUserId("user1")
                .lastName("lastName")
                .mail("user1@sbb.ch")
                .build()
        ));
    Mockito.when(userAdministrationService.getUserPermissions("user1"))
        .thenReturn(List.of(UserPermission.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TTFN)
            .permissionRestrictions(
                Set.of(
                    PermissionRestriction.builder()
                        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .restriction("ch:1:sboid:1")
                        .build()
                )
            )
            .build()));

    UserPermissionCreateModel model = UserPermissionCreateModel
        .builder()
        .sbbUserId("user1")
        .build();

    // when & then
    mvc.perform(post("/v1/users")
            .content(mapper.writeValueAsString(model)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.sbbUserId).value("user1"))
        .andExpect(jsonPath("$." + Fields.mail).value("user1@sbb.ch"));

    Mockito.verify(userAdministrationService, Mockito.times(1)).save(model);
    Mockito.verify(userPermissionDistributor, Mockito.times(1)).pushUserPermissionToKafka(any(UserAdministrationModel.class));
  }

  @Test
  void shouldThrowUserPermissionConflictExceptionOnCreateUserPermission() throws Exception {
    // given
    UserPermissionCreateModel createModel = UserPermissionCreateModel
        .builder()
        .sbbUserId("user1")
        .build();

    Mockito.doThrow(new UserPermissionConflictException(createModel.getSbbUserId()))
        .when(userAdministrationService).save(createModel);

    // when & then
    mvc.perform(post("/v1/users")
            .content(mapper.writeValueAsString(createModel)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("A conflict occurred on UserPermission"))
        .andExpect(jsonPath("$.error").value("User Permission Conflict"))
        .andExpect(jsonPath("$.details").value(hasSize(1)));
  }

  @Test
  void shouldUpdateUserPermission() throws Exception {
    // given
    PermissionModel permission = PermissionModel.builder()
        .application(ApplicationType.TTFN)
        .role(ApplicationRole.WRITER)
        .permissionRestrictions(new ArrayList<>(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:10009"))))
        .build();

    Mockito.when(graphApiService.resolveUsers(List.of("user1")))
        .thenReturn(List.of(
            UserModel.builder()
                .sbbUserId("user1")
                .lastName("lastName")
                .mail("user1@sbb.ch")
                .build()
        ));
    Mockito.when(userAdministrationService.getUserPermissions("user1"))
        .thenReturn(List.of(UserPermissionMapper.toEntity("user1", permission)));

    // when & then
    mvc.perform(put("/v1/users/user1/TTFN").contentType(contentType)
            .content(mapper.writeValueAsString(permission)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sbbUserId").value("user1"))
        .andExpect(jsonPath("$.lastName").value("lastName"))
        .andExpect(jsonPath("$.permissions").value(hasSize(1)))
        .andExpect(jsonPath("$.permissions[0].role").value("WRITER"))
        .andExpect(jsonPath("$.permissions[0].application").value("TTFN"));

    Mockito.verify(userAdministrationService, Mockito.times(1))
        .updatePermission(eq("user1"), eq(ApplicationType.TTFN),
            assertArg(argument -> assertThat(argument).usingRecursiveComparison().isEqualTo(permission)));
    Mockito.verify(userPermissionDistributor, Mockito.times(1))
        .pushUserPermissionToKafka(any(UserAdministrationModel.class));
  }

  @Test
  void getUsersWithSboidsAndApplicationTypesFound() throws Exception {
    // given
    List<String> pageContent = List.of("user1");
    Mockito.when(userAdministrationService.getUserPage(
            any(Pageable.class),
            eq(Set.of("ch:1:sboid:1")),
            eq(Set.of(ApplicationType.LIDI, ApplicationType.TTFN)),
            eq(PermissionRestrictionType.BUSINESS_ORGANISATION))
        )
        .thenReturn(new PageImpl<>(pageContent, Pageable.ofSize(10), 1));
    Mockito.when(graphApiService.resolveUsers(pageContent))
        .thenReturn(List.of(
            UserModel.builder()
                .sbbUserId("user1")
                .accountStatus(UserAccountStatus.ACTIVE)
                .build()
        ));
    Mockito.when(userAdministrationService.getUserPermissions(anyString()))
        .thenReturn(List.of(UserPermission.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TTFN)
            .permissionRestrictions(
                Set.of(
                    PermissionRestriction.builder()
                        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
                        .restriction("ch:1:sboid:1")
                        .build()
                )
            )
            .build()));

    // when & then
    mvc.perform(get("/v1/users")
            .queryParam("page", "0")
            .queryParam("size", "10")
            .queryParam("applicationTypes", "LIDI", "TTFN")
            .queryParam("type", PermissionRestrictionType.BUSINESS_ORGANISATION.name())
            .queryParam("permissionRestrictions", "ch:1:sboid:1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)))
        .andExpect(jsonPath("$.objects[0].sbbUserId").value("user1"))
        .andExpect(jsonPath("$.objects[0].permissions", hasSize(1)));
  }

  @Test
  void getUserDisplayNameExisting() throws Exception {
    // given
    Mockito.when(clientCredentialAdministrationService.getClientCredentialPermission("user1"))
        .thenReturn(List.of(ClientCredentialPermission.builder().alias("Lastname Firstname").build()));

    // when & then
    mvc.perform(get("/v1/users/user1/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value(startsWith("Lastname Firstname")));
  }

  @Test
  void getUserDisplayNameNotExisting() throws Exception {
    // given
    Mockito.when(clientCredentialAdministrationService.getClientCredentialPermission("ATLAS_SYSTEM_USER"))
        .thenReturn(Collections.emptyList());
    Mockito.when(graphApiService.resolveUsers(List.of("ATLAS_SYSTEM_USER")))
        .thenReturn(List.of(UserModel.builder().sbbUserId("ATLAS_SYSTEM_USER").build()));

    // when & then
    mvc.perform(get("/v1/users/ATLAS_SYSTEM_USER/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").doesNotExist());
  }

  @Test
  void getUserDisplayNameForExistingClient() throws Exception {
    // given
    Mockito.when(clientCredentialAdministrationService.getClientCredentialPermission("client-id"))
        .thenReturn(List.of(ClientCredentialPermission.builder().alias("ALIAS").build()));

    // when & then
    mvc.perform(get("/v1/users/client-id/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("ALIAS"));
  }

  @Test
  void shouldGetUserDisplayInformation() throws Exception {
    // given
    Mockito.when(clientCredentialAdministrationService.getClientCredentialPermission("user1"))
        .thenReturn(List.of(ClientCredentialPermission.builder().alias("ALIAS").build()));
    Mockito.when(graphApiService.resolveUsers(Collections.emptyList()))
        .thenReturn(Collections.emptyList());

    // when & then
    mvc.perform(get("/v1/users/display-info?userIds=user1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].sbbUserId").value("user1"))
        .andExpect(jsonPath("$[0].displayName").value("ALIAS"));
  }
}