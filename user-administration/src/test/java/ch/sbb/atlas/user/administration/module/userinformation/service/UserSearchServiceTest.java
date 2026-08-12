package ch.sbb.atlas.user.administration.module.userinformation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.user.administration.module.manualmail.service.UserManualMailOverrideService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserSearchServiceTest {

  private UserSearchService userSearchService;

  @Mock
  private GraphApiService graphApiServiceMock;

  @Mock
  private UserManualMailOverrideService userManualMailOverrideServiceMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userSearchService = new UserSearchService(graphApiServiceMock, userManualMailOverrideServiceMock);
  }

  @Test
  void shouldIncludeUsersFoundOnlyByTheirManualMailOverride() {
    // Given
    doReturn(List.of()).when(graphApiServiceMock).searchUsers("manual@sbb.ch");
    doReturn(List.of("u123456")).when(userManualMailOverrideServiceMock).findUserIdsByMailContaining("manual@sbb.ch");
    doReturn(List.of(UserModel.builder().sbbUserId("u123456").mail("azure@sbb.ch").accountStatus(UserAccountStatus.ACTIVE).build()))
        .when(graphApiServiceMock).resolveUsers(List.of("u123456"));

    // When
    List<UserModel> foundUsers = userSearchService.searchUsers("manual@sbb.ch");

    // Then
    assertThat(foundUsers).extracting(UserModel::getSbbUserId).containsExactly("u123456");
  }

  @Test
  void shouldNotReturnUserTwiceWhenAlreadyFoundViaGraph() {
    // Given
    doReturn(List.of(UserModel.builder().sbbUserId("u123456").mail("azure@sbb.ch").accountStatus(UserAccountStatus.ACTIVE).build()))
        .when(graphApiServiceMock).searchUsers("u123456");
    doReturn(List.of("U123456")).when(userManualMailOverrideServiceMock).findUserIdsByMailContaining("u123456");

    // When
    List<UserModel> foundUsers = userSearchService.searchUsers("u123456");

    // Then
    assertThat(foundUsers).extracting(UserModel::getSbbUserId).containsExactly("u123456");
    verify(graphApiServiceMock, never()).resolveUsers(List.of("U123456"));
  }

  @Test
  void shouldExcludeUsersWithManualMailOverrideWhoseAccountNoLongerExists() {
    // Given
    doReturn(List.of()).when(graphApiServiceMock).searchUsers("manual@sbb.ch");
    doReturn(List.of("u123456")).when(userManualMailOverrideServiceMock).findUserIdsByMailContaining("manual@sbb.ch");
    doReturn(List.of(UserModel.builder().sbbUserId("u123456").accountStatus(UserAccountStatus.DELETED).build()))
        .when(graphApiServiceMock).resolveUsers(List.of("u123456"));

    // When
    List<UserModel> foundUsers = userSearchService.searchUsers("manual@sbb.ch");

    // Then
    assertThat(foundUsers).isEmpty();
  }

  @Test
  void shouldNotResolveUsersWhenNoManualMailOverrideMatches() {
    // Given
    doReturn(List.of(UserModel.builder().sbbUserId("u123456").mail("azure@sbb.ch").accountStatus(UserAccountStatus.ACTIVE).build()))
        .when(graphApiServiceMock).searchUsers("u123456");
    doReturn(List.of()).when(userManualMailOverrideServiceMock).findUserIdsByMailContaining("u123456");

    // When
    List<UserModel> foundUsers = userSearchService.searchUsers("u123456");

    // Then
    assertThat(foundUsers).extracting(UserModel::getSbbUserId).containsExactly("u123456");
    verify(graphApiServiceMock, never()).resolveUsers(List.of("u123456"));
  }

}
