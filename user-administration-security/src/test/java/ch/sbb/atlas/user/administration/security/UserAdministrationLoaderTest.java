package ch.sbb.atlas.user.administration.security;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.business.organisation.SharedBusinessOrganisationConfig;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.transport.company.SharedTransportCompanyConfig;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class UserAdministrationLoaderTest {

  @MockBean
  private SharedBusinessOrganisationConfig sharedBusinessOrganisationConfig;
  @MockBean
  private SharedTransportCompanyConfig sharedTransportCompanyConfig;

  private final PermissionRepository permissionRepository;
  private final UserAdministrationLoader userAdministrationLoader;

  @Autowired
   UserAdministrationLoaderTest(PermissionRepository permissionRepository,
      UserAdministrationLoader userAdministrationLoader) {
    this.permissionRepository = permissionRepository;
    this.userAdministrationLoader = userAdministrationLoader;
  }

  @Test
  void shouldLoadPermissionsFromKafkaToDatabase() {
    // Given
    String userId = "e123456";

    UserAdministrationModel userPermissions = UserAdministrationModel.builder().userId(userId).permissions(Set.of(
        UserAdministrationPermissionModel.builder().application(ApplicationType.TTFN).role(ApplicationRole.SUPERVISOR).build(),
        UserAdministrationPermissionModel.builder().application(ApplicationType.LIDI).role(ApplicationRole.WRITER).restrictions(
            Set.of(UserAdministrationPermissionRestrictionModel.builder().value("ch:1:sboid:12341234")
                .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION).build())).build())).build();

    // When
    userAdministrationLoader.readUserPermissionsFromKafka(userPermissions);

    // Then
    List<Permission> permissions = permissionRepository.findAllByIdentifier(userId);
    assertThat(permissions).hasSize(2);
    assertThat(permissions.stream().filter(i -> i.getApplication() == ApplicationType.TTFN).findFirst().orElseThrow()
        .getPermissionRestrictions()).isEmpty();
    assertThat(permissions.stream().filter(i -> i.getApplication() == ApplicationType.LIDI).findFirst().orElseThrow()
        .getPermissionRestrictions()).hasSize(1);
  }
}
