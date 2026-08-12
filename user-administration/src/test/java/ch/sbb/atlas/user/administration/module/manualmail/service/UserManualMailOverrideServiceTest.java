package ch.sbb.atlas.user.administration.module.manualmail.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMailOverride;
import ch.sbb.atlas.user.administration.module.manualmail.repository.UserManualMailOverrideRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserManualMailOverrideServiceTest {

  private UserManualMailOverrideService userManualMailOverrideService;

  @Mock
  private UserManualMailOverrideRepository userManualMailOverrideRepositoryMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userManualMailOverrideService = new UserManualMailOverrideService(userManualMailOverrideRepositoryMock);
  }

  @Test
  void shouldCreateManualMailWhenNoneExists() {
    // Given
    doReturn(Optional.empty()).when(userManualMailOverrideRepositoryMock).findBySbbUserIdIgnoreCase("u123456");
    ArgumentCaptor<UserManualMailOverride> captor = ArgumentCaptor.forClass(UserManualMailOverride.class);

    // When
    userManualMailOverrideService.setManualMailOverride("u123456", "manual@sbb.ch");

    // Then
    verify(userManualMailOverrideRepositoryMock).save(captor.capture());
    assertThat(captor.getValue().getSbbUserId()).isEqualTo("u123456");
    assertThat(captor.getValue().getMail()).isEqualTo("manual@sbb.ch");
  }

  @Test
  void shouldUpdateExistingManualMailWhenAlreadyPresent() {
    // Given
    UserManualMailOverride existing = UserManualMailOverride.builder().sbbUserId("u123456").mail("old@sbb.ch").build();
    doReturn(Optional.of(existing)).when(userManualMailOverrideRepositoryMock).findBySbbUserIdIgnoreCase("u123456");

    // When
    userManualMailOverrideService.setManualMailOverride("u123456", "new@sbb.ch");

    // Then
    verify(userManualMailOverrideRepositoryMock).save(existing);
    assertThat(existing.getMail()).isEqualTo("new@sbb.ch");
  }

  @Test
  void shouldDoNothingWhenDeletingNonExistingManualMail() {
    // When
    userManualMailOverrideService.deleteManualMailOverride("u123456");

    // Then
    verify(userManualMailOverrideRepositoryMock, times(1)).deleteBySbbUserIdIgnoreCase("u123456");
  }

  @Test
  void shouldFindUserIdsByPartialMail() {
    // Given
    doReturn(List.of(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("manual@sbb.ch").build(),
        UserManualMailOverride.builder().sbbUserId("u999999").mail("manual.other@sbb.ch").build()))
        .when(userManualMailOverrideRepositoryMock).findTop10ByMailContainingIgnoreCase("manual");

    // When
    List<String> userIds = userManualMailOverrideService.findUserIdsByMailContaining("manual");

    // Then
    assertThat(userIds).containsExactly("u123456", "u999999");
  }

  @Test
  void shouldNotSearchUserIdsWhenSearchQueryIsBlank() {
    // When
    List<String> userIds = userManualMailOverrideService.findUserIdsByMailContaining("  ");

    // Then
    assertThat(userIds).isEmpty();
    verifyNoInteractions(userManualMailOverrideRepositoryMock);
  }

}
