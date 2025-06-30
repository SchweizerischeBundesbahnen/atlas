package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class UserAdministrationServiceUpdateTest {

  private static final String SBBUID = "***REMOVED***";

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @Autowired
  private UserAdministrationService userAdministrationService;

  @BeforeEach
  void setUp() {
    UserPermission firstUserPermission = UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.TTFN)
        .sbbUserId(SBBUID)
        .build();
    UserPermission secondUserPermission = UserPermission.builder()
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.LIDI)
        .sbbUserId(SBBUID)
        .build();
    secondUserPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .userPermission(secondUserPermission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction("ch:1:sboid:1000000").build(), PermissionRestriction.builder()
        .userPermission(secondUserPermission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction("ch:1:sboid:1000012").build()));
    userPermissionRepository.saveAll(List.of(firstUserPermission, secondUserPermission));
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldDegradeTtfnSuperVisorToSuperuser() {
    // Given
    PermissionModel editedPermission =
        PermissionModel.builder()
            .application(ApplicationType.TTFN)
            .role(ApplicationRole.SUPER_USER)
            .build();

    // When
    userAdministrationService.updatePermission(SBBUID, ApplicationType.TTFN, editedPermission);

    // Then
    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.SUPER_USER);
    assertThat(ttfnPermissions.getPermissionRestrictions()).isEmpty();

    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.WRITER);
  }

  @Test
  void shouldDegradeTtfnSuperVisorToWriter() {
    // Given
    PermissionModel editedPermission = PermissionModel.builder()
        .application(ApplicationType.TTFN)
        .role(ApplicationRole.WRITER)
        .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:10009")))
        .build();

    // When
    userAdministrationService.updatePermission(SBBUID, ApplicationType.TTFN, editedPermission);

    // Then
    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(ttfnPermissions.getPermissionRestrictions()).hasSize(1);
  }

  @Test
  void shouldUpgradeFromWriterToSuperUserAndClearSboids() {
    // Given
    PermissionModel editedPermissions =
        PermissionModel.builder()
            .application(ApplicationType.LIDI)
            .role(ApplicationRole.SUPER_USER)
            .build();

    // When
    userAdministrationService.updatePermission(SBBUID, ApplicationType.LIDI, editedPermissions);

    // Then
    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.SUPER_USER);
    assertThat(lidiPermissions.getPermissionRestrictions()).isEmpty();

    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.SUPERVISOR);
  }

  @Test
  void shouldUpdateUserPermissionOnReaderDowngrade() {
    // Given
    PermissionModel editedPermissions = PermissionModel.builder()
        .application(ApplicationType.LIDI)
        .role(ApplicationRole.READER)
        .build();

    // When
    userAdministrationService.updatePermission(SBBUID, ApplicationType.LIDI, editedPermissions);

    // Then
    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.READER);
  }

}
