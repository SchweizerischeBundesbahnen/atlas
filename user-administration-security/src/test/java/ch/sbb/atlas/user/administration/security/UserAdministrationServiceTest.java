package ch.sbb.atlas.user.administration.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.user.administration.security.model.ApplicationRole;
import ch.sbb.atlas.user.administration.security.model.ApplicationType;
import ch.sbb.atlas.user.administration.security.model.UserModel;
import ch.sbb.atlas.user.administration.security.model.UserPermissionModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserAdministrationServiceTest {

  @Mock
  private UserPermissionHolder userPermissionHolder;

  private UserAdministrationService userAdministrationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userAdministrationService = new UserAdministrationService(userPermissionHolder);
    when(userPermissionHolder.isAdmin()).thenReturn(false);
    when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");
  }

  @Test
  void shouldAllowCreateToAdminUser() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean permissionsToCreate = userAdministrationService.hasUserPermissionsToCreate(
        BusinessObject.createDummy().build(), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldAllowCreateToSupervisor() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.SUPERVISOR)
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToCreate = userAdministrationService.hasUserPermissionsToCreate(
        BusinessObject.createDummy().build(), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldAllowCreateToSuperUser() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.SUPER_USER)
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToCreate = userAdministrationService.hasUserPermissionsToCreate(
        BusinessObject.createDummy().build(), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldNotAllowCreateToWriterWithNoSboids() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToCreate = userAdministrationService.hasUserPermissionsToCreate(
        BusinessObject.createDummy().build(), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToCreate).isFalse();
  }

  @Test
  void shouldAllowCreateToWriterCorrectSboids() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .sboids(
                                                                                               Set.of(
                                                                                                   "sboid"))
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToCreate = userAdministrationService.hasUserPermissionsToCreate(
        BusinessObject.createDummy().build(), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToCreate).isTrue();
  }

  @Test
  void shouldAllowUpdateToAdminUser() {
    // Given
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("previousValue").build()),
        ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldAllowUpdateToSupervisor() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.SUPERVISOR)
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("previousValue").build()),
        ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldAllowUpdateToSuperUser() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.SUPER_USER)
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("previousValue").build()),
        ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldNotAllowUpdateToWriterWithoutSboids() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("previousValue").build()),
        ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isFalse();
  }

  @Test
  void shouldAllowUpdateToWriterWithSboid() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .sboids(
                                                                                               Set.of(
                                                                                                   "sboid"))
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy().build(),
        List.of(BusinessObject.createDummy().anotherValue("previousValue").build()),
        ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldNotAllowUpdateToWriterWithOnlyOneOfTwoRequired() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .sboids(
                                                                                               Set.of(
                                                                                                   "sboid1"))
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy()
                      .businessOrganisation("sboid1")
                      .validFrom(LocalDate.of(2020, 1, 1))
                      .validTo(LocalDate.of(2022, 12, 31))
                      .build(),
        List.of(BusinessObject.createDummy().businessOrganisation("sboid1").build(),
            BusinessObject.createDummy()
                          .businessOrganisation("sboid2")
                          .validFrom(LocalDate.of(2021, 1, 1))
                          .validTo(LocalDate.of(2022, 12, 31))
                          .build()), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isFalse();
  }

  @Test
  void shouldAllowUpdateToWriterWithTwoRequiredSboids() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .sboids(
                                                                                               Set.of(
                                                                                                   "sboid1",
                                                                                                   "sboid2"))
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy()
                      .businessOrganisation("sboid1")
                      .validFrom(LocalDate.of(2020, 1, 1))
                      .validTo(LocalDate.of(2022, 12, 31))
                      .build(),
        List.of(BusinessObject.createDummy().businessOrganisation("sboid1").build(),
            BusinessObject.createDummy()
                          .businessOrganisation("sboid2")
                          .validFrom(LocalDate.of(2021, 1, 1))
                          .validTo(LocalDate.of(2022, 12, 31))
                          .build()), ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

  @Test
  void shouldAllowUpdateToWriterHandingOverObjectToOtherBusinessOrganisation() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(UserModel.builder()
                                                                    .sbbUserId("e123456")
                                                                    .permissions(Set.of(
                                                                        UserPermissionModel.builder()
                                                                                           .application(
                                                                                               ApplicationType.LIDI)
                                                                                           .role(
                                                                                               ApplicationRole.WRITER)
                                                                                           .sboids(
                                                                                               Set.of(
                                                                                                   "sboid1"))
                                                                                           .build()))
                                                                    .build());

    // When
    boolean permissionsToUpdate = userAdministrationService.hasUserPermissionsToUpdate(
        BusinessObject.createDummy()
                      .businessOrganisation("sboid2")
                      .validFrom(LocalDate.of(2021, 1, 1))
                      .validTo(LocalDate.of(2022, 12, 31))
                      .build(),
        List.of(BusinessObject.createDummy().businessOrganisation("sboid1").build()),
        ApplicationType.LIDI);

    // Then
    assertThat(permissionsToUpdate).isTrue();
  }

}