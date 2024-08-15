package ch.sbb.importservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BulkImportUserAdministrationServiceTest {

  @Mock
  private UserPermissionHolder userPermissionHolder;

  private BulkImportUserAdministrationService bulkImportUserAdministrationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    bulkImportUserAdministrationService = new BulkImportUserAdministrationService(userPermissionHolder);

    when(userPermissionHolder.isAdmin()).thenReturn(false);
    when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");
  }

  @Test
  void shouldAllowBulkImportToAdminUser() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean permissionsForBulkImport = bulkImportUserAdministrationService.hasPermissionsForBulkImport(ApplicationType.SEPODI);

    // Then
    assertThat(permissionsForBulkImport).isTrue();
  }

  @Test
  void shouldAllowBulkImportToSupervisor() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.SUPERVISOR)
                .build()))
        .build()));

    // When
    boolean permissionsForBulkImport = bulkImportUserAdministrationService.hasPermissionsForBulkImport(ApplicationType.SEPODI);

    // Then
    assertThat(permissionsForBulkImport).isTrue();
  }

  @Test
  void shouldNotAllowBulkImportToWriter() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .build()))
        .build()));

    // When
    boolean permissionsForBulkImport = bulkImportUserAdministrationService.hasPermissionsForBulkImport(ApplicationType.SEPODI);

    // Then
    assertThat(permissionsForBulkImport).isFalse();
  }

  @Test
  void shouldAllowBulkImportToWriterWithExplicitPermission() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.SEPODI)
                .role(ApplicationRole.WRITER)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .restrictionType(PermissionRestrictionType.BULK_IMPORT)
                    .value(Boolean.TRUE.toString())
                    .build()))
                .build()))
        .build()));

    // When
    boolean permissionsForBulkImport = bulkImportUserAdministrationService.hasPermissionsForBulkImport(ApplicationType.SEPODI);

    // Then
    assertThat(permissionsForBulkImport).isTrue();
  }

}