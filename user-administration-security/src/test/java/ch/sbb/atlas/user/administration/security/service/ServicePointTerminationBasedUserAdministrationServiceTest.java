package ch.sbb.atlas.user.administration.security.service;

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

class ServicePointTerminationBasedUserAdministrationServiceTest {

  @Mock
  private UserPermissionHolder userPermissionHolder;

  private ServicePointTerminationBasedUserAdministrationService servicePointTerminationBasedUserAdministrationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servicePointTerminationBasedUserAdministrationService = new ServicePointTerminationBasedUserAdministrationService(
        userPermissionHolder);
    when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");
  }

  @Test
  void shouldReturnTrueWhenUserHasInfoPlusTerminationVotePermission() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .role(ApplicationRole.READER)
                .application(ApplicationType.SEPODI)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .restrictionType(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE)
                    .value("true")
                    .build()))
                .build()))
        .build()));

    // When
    boolean result = servicePointTerminationBasedUserAdministrationService.hasUserInfoPlusTerminationVotePermission();

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserHasNotInfoPlusTerminationVotePermission() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .role(ApplicationRole.READER)
                .application(ApplicationType.SEPODI)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .restrictionType(PermissionRestrictionType.INFO_PLUS_TERMINATION_VOTE)
                    .value("false")
                    .build()))
                .build()))
        .build()));

    // When
    boolean result = servicePointTerminationBasedUserAdministrationService.hasUserInfoPlusTerminationVotePermission();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserHasNovaTerminationVotePermission() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .role(ApplicationRole.READER)
                .application(ApplicationType.SEPODI)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .restrictionType(PermissionRestrictionType.NOVA_TERMINATION_VOTE)
                    .value("true")
                    .build()))
                .build()))
        .build()));

    // When
    boolean result = servicePointTerminationBasedUserAdministrationService.hasUserNovaTerminationVotePermission();

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserHasNotNovaVotePermission() {
    // Given
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .role(ApplicationRole.READER)
                .application(ApplicationType.SEPODI)
                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                    .restrictionType(PermissionRestrictionType.NOVA_TERMINATION_VOTE)
                    .value("false")
                    .build()))
                .build()))
        .build()));

    // When
    boolean result = servicePointTerminationBasedUserAdministrationService.hasUserInfoPlusTerminationVotePermission();

    // Then
    assertThat(result).isFalse();
  }
}
