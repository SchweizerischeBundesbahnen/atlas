package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class UserAdministrationServiceTest {

  private UserAdministrationService userAdministrationService;

  @Mock
  private UserPermissionRepository userPermissionRepositoryMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userAdministrationService = new UserAdministrationService(userPermissionRepositoryMock);
  }

  @Test
  void shouldThrowUserPemissionConflictExceptionOnValidatePermissionExistence() {
    doReturn(true).when(userPermissionRepositoryMock).existsBySbbUserIdIgnoreCase(anyString());
    UserPermissionCreateModel createModel = UserPermissionCreateModel.builder().sbbUserId("u123456").build();

    assertThatExceptionOfType(UserPermissionConflictException.class).isThrownBy(
        () -> userAdministrationService.save(createModel));
  }


  @Test
  void testFilterForUserInAtlas() {
    PermissionModel permissionModel = PermissionModel.builder()
            .role(ApplicationRole.WRITER)
            .application(ApplicationType.SEPODI)
            .permissionRestrictions(Collections.emptyList()).build();

    UserModel user1 =  UserModel.builder()
            .sbbUserId("u239096")
            .firstName("daniel")
            .lastName("djukic")
            .mail("daniel@daniel.com")
            .accountStatus(UserAccountStatus.ACTIVE)
            .permissions(Set.of(permissionModel))
            .build();

    UserModel user2 =  UserModel.builder()
            .sbbUserId("u123456")
            .firstName("David")
            .lastName("Kronbacher")
            .mail("david@david.com")
            .accountStatus(UserAccountStatus.ACTIVE)
            .permissions(Set.of(permissionModel))
            .build();

    UserModel user3 =  UserModel.builder()
            .sbbUserId("u111111")
            .firstName("Elena")
            .lastName("Müller")
            .mail("elena@elena.com")
            .accountStatus(UserAccountStatus.ACTIVE)
            .permissions(Set.of(permissionModel))
            .build();

    List<UserModel> foundUsers = Arrays.asList(user1, user2, user3);

    ApplicationType applicationType = ApplicationType.SEPODI;
    UserPermission permissionUser1 = UserPermission.builder()
            .sbbUserId(user1.getSbbUserId())
            .role(ApplicationRole.WRITER)
            .permissionRestrictions(Collections.emptySet())
            .build();

    UserPermission permissionUser2 = UserPermission.builder()
            .sbbUserId(user1.getSbbUserId())
            .role(ApplicationRole.WRITER)
            .permissionRestrictions(Collections.emptySet())
            .build();

    UserPermission permissionUser3 = UserPermission.builder()
            .sbbUserId(user1.getSbbUserId())
            .role(ApplicationRole.WRITER)
            .permissionRestrictions(Collections.emptySet())
            .build();

    when(userPermissionRepositoryMock.findBySbbUserIdIgnoreCaseAndApplication(user1.getSbbUserId(), applicationType))
            .thenReturn(Optional.of(permissionUser1));
    when(userPermissionRepositoryMock.findBySbbUserIdIgnoreCaseAndApplication(user2.getSbbUserId(), applicationType))
            .thenReturn(Optional.of(permissionUser2));
    when(userPermissionRepositoryMock.findBySbbUserIdIgnoreCaseAndApplication(user3.getSbbUserId(), applicationType))
            .thenReturn(Optional.of(permissionUser3));

    List<UserModel> permittedUsers = userAdministrationService.filterForUserInAtlas(foundUsers, applicationType);

    assertThat(1).isEqualTo(permittedUsers.size());
    assertThat(user1.getSbbUserId()).isEqualTo(permittedUsers.getFirst().getSbbUserId());

    verify(userPermissionRepositoryMock, times(3)).findBySbbUserIdIgnoreCaseAndApplication(anyString(), eq(applicationType));
  }
}
