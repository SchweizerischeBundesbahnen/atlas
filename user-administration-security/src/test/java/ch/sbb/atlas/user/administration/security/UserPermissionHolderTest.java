package ch.sbb.atlas.user.administration.security;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.SharedBusinessOrganisationConfig;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithAdminMockJwtAuthentication;
import ch.sbb.atlas.transport.company.SharedTransportCompanyConfig;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class UserPermissionHolderTest {

  @MockBean
  private SharedBusinessOrganisationConfig sharedBusinessOrganisationConfig;
  @MockBean
  private SharedTransportCompanyConfig sharedTransportCompanyConfig;

  private final UserPermissionHolder userPermissionHolder;
  private final PermissionRepository permissionRepository;

  @Autowired
  public UserPermissionHolderTest(UserPermissionHolder userPermissionHolder, PermissionRepository permissionRepository) {
    this.userPermissionHolder = userPermissionHolder;
    this.permissionRepository = permissionRepository;
  }

  @BeforeEach
  void setUp() {
    Permission permission = Permission.builder()
        .identifier(WithAdminMockJwtAuthentication.SBB_UID)
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.SEPODI)
        .build();
    PermissionRestriction permissionRestriction = PermissionRestriction.builder()
        .restriction("sboid")
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .permission(permission)
        .build();
    permission.setPermissionRestrictions(Set.of(permissionRestriction));
    permissionRepository.save(permission);
  }

  @AfterEach
  void tearDown() {
    permissionRepository.deleteAll();
  }

  @Test
  void shouldLoadUserFromPermissionTable() {
    Optional<UserAdministrationModel> currentUser = userPermissionHolder.getCurrentUser();
    assertThat(currentUser).isPresent();
    assertThat(currentUser.get().getUserId()).isEqualTo(WithAdminMockJwtAuthentication.SBB_UID);
    assertThat(currentUser.get().getPermissions().iterator().next().getRestrictions().iterator().next().getRestrictionType())
        .isEqualTo(PermissionRestrictionType.BUSINESS_ORGANISATION);
  }
}