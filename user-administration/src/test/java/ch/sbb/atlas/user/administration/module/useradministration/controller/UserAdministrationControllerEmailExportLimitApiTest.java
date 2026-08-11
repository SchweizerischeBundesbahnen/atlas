package ch.sbb.atlas.user.administration.module.useradministration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Separate top-level test class (rather than a {@code @Nested} class inside
 * {@link UserAdministrationControllerApiTest}) so the lowered {@code max-users} limit only
 * applies to its own Spring context, keeping the default-limit tests unaffected.
 */
@DisplayName("GET v1/users/emails - max-users limit")
@TestPropertySource(properties = "atlas.user-administration.emails.max-users=2")
class UserAdministrationControllerEmailExportLimitApiTest extends BaseControllerApiTest {

  private static final String SBOID = "ch:1:sboid:20009";

  @MockitoBean
  private GraphServiceClient graphClient;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @BeforeEach
  void setUp() {
    UsersRequestBuilder usersRequestBuilderMock = mock(UsersRequestBuilder.class);
    UserCollectionResponse userCollectionResponseMock = mock(UserCollectionResponse.class);
    when(userCollectionResponseMock.getValue()).thenReturn(List.of(new User()));
    when(usersRequestBuilderMock.get(any())).thenReturn(userCollectionResponseMock);
    when(graphClient.users()).thenReturn(usersRequestBuilderMock);

    for (int i = 0; i < 3; i++) {
      UserPermission userPermission = UserPermission.builder()
          .sbbUserId("u3%04d".formatted(i))
          .role(ApplicationRole.WRITER)
          .application(ApplicationType.SEPODI)
          .build();
      userPermission.getPermissionRestrictions().add(PermissionRestriction.builder()
          .userPermission(userPermission)
          .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
          .restriction(SBOID)
          .build());
      userPermissionRepository.saveAndFlush(userPermission);
    }
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldRejectRequestWhenMoreUsersThanConfiguredLimit() throws Exception {
    // given: the property override above lowers the limit to 2, but 3 users match the filter

    // when & then
    mvc.perform(get("/v1/users/emails")
            .queryParam("permissionRestrictions", SBOID)
            .queryParam("type", "BUSINESS_ORGANISATION")
            .queryParam("applicationTypes", "SEPODI"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details[0].displayInfo.code").value("USER_ADMIN.TOO_MANY_EMAILS_FOR_CLIPBOARD"));
  }
}
