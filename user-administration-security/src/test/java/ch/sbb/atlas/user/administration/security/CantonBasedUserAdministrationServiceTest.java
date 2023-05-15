package ch.sbb.atlas.user.administration.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CantonBasedUserAdministrationServiceTest {

  @Mock
  private UserPermissionHolder userPermissionHolder;

  private CantonBasedUserAdministrationService cantonBasedUserAdministrationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cantonBasedUserAdministrationService = new CantonBasedUserAdministrationService(userPermissionHolder);
    when(userPermissionHolder.isAdmin()).thenReturn(false);
    when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");
  }

  @Test
  void shouldAllowRead() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastExplicitReader(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isTrue();
  }

  @Test
  void shouldNotAllowReadToDefaultReader() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.READER)
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastExplicitReader(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isFalse();
  }

  @Test
  void shouldAllowReadToExplicitReader() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.EXPLICIT_READER)
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastExplicitReader(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isTrue();
  }

  @Test
  void shouldAllowCreateToWriterWithCorrectCanton() {
    CantonObject dummy = CantonObject.createDummy();

    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .swissCantons(Set.of(dummy.getSwissCanton()))
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastWriter(ApplicationType.TIMETABLE_HEARING,
        dummy);

    // Then
    assertThat(permissionsGranted).isTrue();
  }

  @Test
  void shouldNotAllowCreateToWriterWithFalseCanton() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .swissCantons(Set.of(SwissCanton.SCHWYZ))
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastWriter(ApplicationType.TIMETABLE_HEARING,
        CantonObject.createDummy());

    // Then
    assertThat(permissionsGranted).isFalse();
  }

  @Test
  void shouldAllowTimetableHearingStatementUpdateToAnyCantonEvenWithoutWriteRights() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .swissCantons(Set.of(SwissCanton.SCHWYZ))
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isStatementCantonUpdate(ApplicationType.TIMETABLE_HEARING,
        CantonObject.createDummy());

    // Then
    assertThat(permissionsGranted).isTrue();
  }

  @Test
  void shouldNotAllowTimetableHearingStatementUpdateIfApplicationTypeNotTimetableHearing() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .swissCantons(Set.of(SwissCanton.SCHWYZ))
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isStatementCantonUpdate(ApplicationType.LIDI,
        CantonObject.createDummy());

    // Then
    assertThat(permissionsGranted).isFalse();
  }

  @Test
  void shouldNotAllowNoRoleDoSupervisorStuff() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.empty());

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isFalse();
  }

  @Test
  void shouldNotAllowReaderDoSupervisorStuff() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.EXPLICIT_READER)
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isFalse();
  }

  @Test
  void shouldNotAllowWriterDoSupervisorStuff() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .swissCantons(Set.of(SwissCanton.BERN))
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isFalse();
  }

  @Test
  void shouldAllowSupervisorDoSupervisorStuff() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .sbbUserId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .swissCantons(Set.of(SwissCanton.BERN))
            .build()))
        .build()));

    // When
    boolean permissionsGranted = cantonBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.TIMETABLE_HEARING);

    // Then
    assertThat(permissionsGranted).isFalse();
  }

}