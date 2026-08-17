package ch.sbb.atlas.user.administration.module.manualmail.validation;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.user.administration.module.manualmail.exception.MailAlreadyInUseException;
import ch.sbb.atlas.user.administration.module.manualmail.service.UserManualMailOverrideService;
import ch.sbb.atlas.user.administration.module.userinformation.service.GraphApiService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ManualMailOverrideValidationServiceTest {

  private ManualMailOverrideValidationService manualMailOverrideValidationService;

  @Mock
  private UserManualMailOverrideService userManualMailOverrideServiceMock;

  @Mock
  private GraphApiService graphApiServiceMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    manualMailOverrideValidationService = new ManualMailOverrideValidationService(userManualMailOverrideServiceMock,
        graphApiServiceMock);

    doReturn(Optional.empty()).when(userManualMailOverrideServiceMock).findUserIdByMail(any());
    doReturn(List.of()).when(graphApiServiceMock).searchUserByMail(any());
  }

  @Test
  void shouldAllowMailWhichIsNeitherKnownInGraphApiNorInDatabase() {
    // When & Then
    assertThatNoException().isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "free@sbb.ch"));
  }

  @Test
  void shouldRejectMailAlreadyUsedAsManualMailOverrideOfAnotherUser() {
    // Given
    doReturn(Optional.of("e654321")).when(userManualMailOverrideServiceMock).findUserIdByMail("taken@sbb.ch");

    // When & Then
    assertThatExceptionOfType(MailAlreadyInUseException.class).isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "taken@sbb.ch"));
  }

  @Test
  void shouldAllowMailAlreadyUsedAsManualMailOverrideOfTheSameUser() {
    // Given
    doReturn(Optional.of("U123456")).when(userManualMailOverrideServiceMock).findUserIdByMail("taken@sbb.ch");

    // When & Then
    assertThatNoException().isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "taken@sbb.ch"));
  }

  @Test
  void shouldNotQueryGraphApiWhenMailIsAlreadyUsedInDatabase() {
    // Given
    doReturn(Optional.of("e654321")).when(userManualMailOverrideServiceMock).findUserIdByMail("taken@sbb.ch");

    // When
    assertThatExceptionOfType(MailAlreadyInUseException.class).isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "taken@sbb.ch"));

    // Then
    verify(graphApiServiceMock, never()).searchUserByMail(any());
  }

  @Test
  void shouldRejectMailWhichIsTheAzureMailOfAnotherUser() {
    // Given
    doReturn(List.of(UserModel.builder().sbbUserId("e654321").mail("taken@sbb.ch").build()))
        .when(graphApiServiceMock).searchUserByMail("taken@sbb.ch");

    // When & Then
    assertThatExceptionOfType(MailAlreadyInUseException.class).isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "taken@sbb.ch"));
  }

  @Test
  void shouldRejectMailWhichIsTheAzureMailOfAnotherUserIgnoringCase() {
    // Given
    doReturn(List.of(UserModel.builder().sbbUserId("e654321").mail("Taken@SBB.ch").build()))
        .when(graphApiServiceMock).searchUserByMail("taken@sbb.ch");

    // When & Then
    assertThatExceptionOfType(MailAlreadyInUseException.class).isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "taken@sbb.ch"));
  }

  @Test
  void shouldAllowMailWhichIsTheAzureMailOfTheSameUser() {
    // Given
    doReturn(List.of(UserModel.builder().sbbUserId("u123456").mail("u123456@sbb.ch").build()))
        .when(graphApiServiceMock).searchUserByMail("u123456@sbb.ch");

    // When & Then
    assertThatNoException().isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "u123456@sbb.ch"));
  }

  @Test
  void shouldAllowMailWhenGraphApiOnlyReturnsFuzzyMatchesWithDifferentMail() {
    // Given: the Graph API search is a fuzzy search and may return users not owning the given mail
    doReturn(List.of(UserModel.builder().sbbUserId("e654321").mail("other@sbb.ch").build()))
        .when(graphApiServiceMock).searchUserByMail("free@sbb.ch");

    // When & Then
    assertThatNoException().isThrownBy(
        () -> manualMailOverrideValidationService.validateMailNotInUse("u123456", "free@sbb.ch"));
  }

}
