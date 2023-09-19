package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.CantonPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.CountryPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
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

  private static final String SBBUID = "u236171";

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
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            PermissionModel.builder()
                .application(ApplicationType.TTFN)
                .role(ApplicationRole.SUPER_USER)
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

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
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            PermissionModel.builder()
                .application(ApplicationType.TTFN)
                .role(ApplicationRole.WRITER)
                .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:10009")))
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission ttfnPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TTFN).orElseThrow();
    assertThat(ttfnPermissions.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(ttfnPermissions.getPermissionRestrictions()).hasSize(1);
  }

  @Test
  void shouldUpgradeFromWriterToSuperUserAndClearSboids() {
    // Given
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            PermissionModel.builder()
                .application(ApplicationType.LIDI)
                .role(ApplicationRole.SUPER_USER)
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

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
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            PermissionModel.builder()
                .application(ApplicationType.LIDI)
                .role(ApplicationRole.READER)
                .build()))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.READER);
  }

  @Test
  void shouldAddAdditionalApplicationPermissionsOnEdit() {
    // Given
    UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
        .sbbUserId(SBBUID)
        .permissions(List.of(
            PermissionModel.builder()
                .application(ApplicationType.LIDI)
                .role(ApplicationRole.SUPER_USER)
                .build(),
            PermissionModel.builder()
                .application(ApplicationType.TIMETABLE_HEARING)
                .role(ApplicationRole.WRITER)
                .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.BERN),
                    new CantonPermissionRestrictionModel(SwissCanton.LUCERNE)))
                .build(),
            PermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .permissionRestrictions(List.of(new CountryPermissionRestrictionModel(Country.AFGHANISTAN),
                    new CountryPermissionRestrictionModel(Country.SWITZERLAND)))
                .build()
        ))
        .build();

    // When
    userAdministrationService.updateUser(editedPermissions);

    // Then
    UserPermission lidiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.LIDI).orElseThrow();
    assertThat(lidiPermissions.getRole()).isEqualTo(ApplicationRole.SUPER_USER);

    UserPermission hearingPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.TIMETABLE_HEARING).orElseThrow();
    assertThat(hearingPermissions.getPermissionRestrictions()).hasSize(2);

    UserPermission sepodiPermissions = userAdministrationService.getCurrentUserPermission(SBBUID,
        ApplicationType.SEPODI).orElseThrow();
    assertThat(sepodiPermissions.getPermissionRestrictions()).hasSize(2);
  }
}
