package ch.sbb.atlas.user.administration.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.model.BoContactAssociated;
import ch.sbb.atlas.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class BoUserMailCheckServiceTest {

  private MockedStatic<UserService> userServiceMock;

  private BoUserMailCheckService boUserMailCheckService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    boUserMailCheckService = new BoUserMailCheckService();
    userServiceMock = Mockito.mockStatic(UserService.class);

    userServiceMock.when(UserService::getPreferredUsername).thenReturn("user@yb.com");
    userServiceMock.when(UserService::getUserIdentifier).thenReturn("u123456");
  }

  @AfterEach
  void tearDown() {
    userServiceMock.close();
  }

  @Test
  void shouldReturnTrueWhenMailsAreMatching() {
    //given
    BoContactAssociated boMail = mock(BoContactAssociated.class);
    when(boMail.getBoContactMail()).thenReturn("user@yb.com");

    //when
    boolean result = boUserMailCheckService.isCurrentUserAssignedTo(boMail);

    //then
    assertThat(result).isTrue();
    userServiceMock.verify(UserService::getPreferredUsername, times(1));
  }

  @Test
  void shouldReturnFalseWhenMailsAreNotMatching() {
    //given
    BoContactAssociated boMail = mock(BoContactAssociated.class);
    when(boMail.getBoContactMail()).thenReturn("fc@zueri.com");
    //when
    boolean result = boUserMailCheckService.isCurrentUserAssignedTo(boMail);
    //then
    assertThat(result).isFalse();
    userServiceMock.verify(UserService::getPreferredUsername, times(1));
  }

  @Test
  void shouldReturnTrueWhenMailsAreNotMatchingButSbbuidIs() {
    //given
    BoContactAssociated boMail = mock(BoContactAssociated.class);
    when(boMail.getBoContactMail()).thenReturn("u123456@yb.com");
    when(boMail.getBoContactSbbuid()).thenReturn("u123456");

    //when
    boolean result = boUserMailCheckService.isCurrentUserAssignedTo(boMail);

    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseWhenSbbUidCheckedAgainstBoSbbuidNull() {
    //given
    userServiceMock.when(UserService::getUserIdentifier).thenReturn(null);
    //when
    boolean result = boUserMailCheckService.isCurrentUserSbbUidAssignedTo(null);
    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseWhenSbbUidCheckedAgainstBoSbbuidDoesNotMatch() {
    //when
    boolean result = boUserMailCheckService.isCurrentUserSbbUidAssignedTo("e123456");
    //then
    assertThat(result).isFalse();
    userServiceMock.verify(UserService::getUserIdentifier, times(1));
  }

  @Test
  void shouldReturnTrueWhenSbbUidCheckedAgainstCorrectBoSbbuid() {
    //when
    boolean result = boUserMailCheckService.isCurrentUserSbbUidAssignedTo("u123456");
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseWhenMailNullCheckedAgainstNull() {
    //given
    userServiceMock.when(UserService::getPreferredUsername).thenReturn(null);
    //when
    boolean result = boUserMailCheckService.isCurrentUserMailAssignedTo(null);
    //then
    assertThat(result).isFalse();
  }
}
