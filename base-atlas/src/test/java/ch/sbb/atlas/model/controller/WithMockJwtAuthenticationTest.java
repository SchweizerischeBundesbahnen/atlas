package ch.sbb.atlas.model.controller;

import static ch.sbb.atlas.service.UserService.SBBUID_CLAIM;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockAccountType;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockUser;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class WithMockJwtAuthenticationTest {

  @MockitoBean
  private BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  @MockitoBean
  private TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  @Test
  void shouldBeAdminPerDefaultInIntegrationTests() {
    assertThat(UserService.getRoles()).containsExactlyInAnyOrder(Role.ATLAS_ADMIN, Role.ATLAS_INTERNAL);

    assertThat(UserService.getUserIdentifier()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);

    // Standard users have their name in the preferredUsername, corresponding to their actual mail
    assertThat(UserService.getPreferredUsername()).isEqualTo("test.user@sbb.ch");
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldBeStandardUser() {
    assertThat(UserService.getRoles()).containsExactlyInAnyOrder(Role.ATLAS_INTERNAL);

    Jwt accessToken = UserService.getAccessToken();
    assertThat(UserService.isClientCredentialAuthentication(accessToken)).isFalse();
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldBeUnauthorizedUser() {
    assertThat(UserService.getRoles()).containsExactlyInAnyOrder(Role.ATLAS_ROLES_UNAUTHORIZED_KEY, Role.ATLAS_INTERNAL);

    assertThat(UserService.getAccessToken().getClaimAsString(SBBUID_CLAIM)).isNull();
    assertThat(UserService.getUserIdentifier()).isEqualTo("unauthorized-client-id");
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD, user = MockUser.CLIENT_CREDENTIAL)
  void shouldBeStandardClientCredentialUser() {
    assertThat(UserService.getRoles()).containsExactlyInAnyOrder(Role.ATLAS_INTERNAL);

    Jwt accessToken = UserService.getAccessToken();
    assertThat(UserService.isClientCredentialAuthentication(accessToken)).isTrue();

    // Client credential apps don't have any mail / username
    assertThat(UserService.getPreferredUsername()).isNull();

    assertThat(UserService.getUserIdentifier()).isEqualTo("client-id");
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD, user = MockUser.USER, accountType = MockAccountType.GUEST)
  void shouldBeExternalGuestUser() {
    assertThat(UserService.getRoles()).containsExactlyInAnyOrder(Role.ATLAS_INTERNAL);

    assertThat(UserService.getUserIdentifier()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
    // Guest users don't have their actual mail as preferred username but rather its just their sbbuid with the domain
    assertThat(UserService.getPreferredUsername()).isEqualTo("e123456@sbb.ch");
  }
}
