package ch.sbb.atlas.user.administration.module.manualmail.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMail;
import ch.sbb.atlas.user.administration.module.manualmail.repository.UserManualMailRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserManualMailServiceTest {

  private UserManualMailService userManualMailService;

  @Mock
  private UserManualMailRepository userManualMailRepositoryMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userManualMailService = new UserManualMailService(userManualMailRepositoryMock);
  }

  @Test
  void shouldCreateManualMailWhenNoneExists() {
    // Given
    doReturn(Optional.empty()).when(userManualMailRepositoryMock).findBySbbUserIdIgnoreCase("u123456");
    ArgumentCaptor<UserManualMail> captor = ArgumentCaptor.forClass(UserManualMail.class);

    // When
    userManualMailService.upsert("u123456", "manual@sbb.ch");

    // Then
    verify(userManualMailRepositoryMock).save(captor.capture());
    assertThat(captor.getValue().getSbbUserId()).isEqualTo("u123456");
    assertThat(captor.getValue().getMail()).isEqualTo("manual@sbb.ch");
  }

  @Test
  void shouldUpdateExistingManualMailWhenAlreadyPresent() {
    // Given
    UserManualMail existing = UserManualMail.builder().sbbUserId("u123456").mail("old@sbb.ch").build();
    doReturn(Optional.of(existing)).when(userManualMailRepositoryMock).findBySbbUserIdIgnoreCase("u123456");

    // When
    userManualMailService.upsert("u123456", "new@sbb.ch");

    // Then
    verify(userManualMailRepositoryMock).save(existing);
    assertThat(existing.getMail()).isEqualTo("new@sbb.ch");
  }

  @Test
  void shouldDeleteManualMailWhenBlankValueIsProvided() {
    // When
    userManualMailService.upsert("u123456", "   ");

    // Then
    verify(userManualMailRepositoryMock).deleteBySbbUserIdIgnoreCase("u123456");
    verify(userManualMailRepositoryMock, never()).save(any());
  }

  @Test
  void shouldDoNothingWhenDeletingNonExistingManualMail() {
    // When
    userManualMailService.delete("u123456");

    // Then
    verify(userManualMailRepositoryMock, times(1)).deleteBySbbUserIdIgnoreCase("u123456");
  }

}
