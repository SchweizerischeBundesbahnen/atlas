package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.api.user.administration.UserPermissionModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class UserAdministrationServiceUpdateTest {

  private static final String SBBUID = "u236171";

  @Autowired
  private UserPermissionRepository userPermissionRepository;

  @Autowired
  private UserAdministrationService userAdministrationService;

  @BeforeEach
  void setUp() {
    userPermissionRepository.saveAll(List.of(UserPermission.builder()
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.TTFN)
        .sbbUserId(SBBUID)
        .build(), UserPermission.builder()
        .role(
            ApplicationRole.WRITER)
        .application(
            ApplicationType.LIDI)
        .sboid(Set.of(
            "ch:1:sboid:1000000",
            "ch:1:sboid:1000012"))
        .sbbUserId(
            SBBUID)
        .build()));
  }

  @AfterEach
  void tearDown() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldDegradeTtfnSuperVisorToSuperuser() {
    // Given
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            UserPermissionModel.builder()
                .application(
                    ApplicationType.TTFN)
                .role(
                    ApplicationRole.SUPER_USER)
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.SUPER_USER);
    assertThat(ttfnPermissions.getSboid()).isEmpty();

    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.WRITER);
  }

  @Test
  void shouldDegradeTtfnSuperVisorToWriter() {
    // Given
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            UserPermissionModel.builder()
                .application(
                    ApplicationType.TTFN)
                .role(
                    ApplicationRole.WRITER)
                .sboids(
                    List.of(
                        "ch:1:sboid:10009"))
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(ttfnPermissions.getSboid()).hasSize(1);
  }

  @Test
  void shouldUpgradeFromWriterToSuperUserAndClearSboids() {
    // Given
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            UserPermissionModel.builder()
                .application(
                    ApplicationType.LIDI)
                .role(
                    ApplicationRole.SUPER_USER)
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.SUPER_USER);
    assertThat(lidiPermissions.getSboid()).isEmpty();

    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.SUPERVISOR);
  }

  @Test
  void shouldUpdateUserPermissionOnReaderDowngrade() {
    // Given
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            UserPermissionModel.builder()
                .application(
                    ApplicationType.LIDI)
                .role(
                    ApplicationRole.READER)
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.READER);
  }
}
