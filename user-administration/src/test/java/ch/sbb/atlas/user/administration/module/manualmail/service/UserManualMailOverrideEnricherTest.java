package ch.sbb.atlas.user.administration.module.manualmail.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.user.administration.UserModel;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserManualMailOverrideEnricherTest {

  private UserManualMailEnricher userManualMailEnricher;

  @Mock
  private UserManualMailOverrideService userManualMailOverrideServiceMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userManualMailEnricher = new UserManualMailEnricher(userManualMailOverrideServiceMock);
  }

  @Test
  void shouldSetMailToManualMailAndKeepOriginalMailWhenOverrideExists() {
    // Given
    UserModel user1 = UserModel.builder().sbbUserId("u111111").mail("one@sbb.ch").build();
    UserModel user2 = UserModel.builder().sbbUserId("u222222").mail("two@sbb.ch").build();
    doReturn(Map.of("u111111", "manual-one@sbb.ch")).when(userManualMailOverrideServiceMock)
        .getMailsByUserIds(List.of("u111111", "u222222"));

    // When
    List<UserModel> enriched = userManualMailEnricher.enrich(List.of(user1, user2));

    // Then
    assertThat(enriched).extracting(UserModel::getMail).containsExactly("manual-one@sbb.ch", "two@sbb.ch");
    assertThat(enriched).extracting(UserModel::getOriginalMail).containsExactly("one@sbb.ch", "two@sbb.ch");
  }

  @Test
  void shouldLeaveMailEqualToOriginalMailWhenNoOverrideExists() {
    // Given
    UserModel user = UserModel.builder().sbbUserId("u333333").mail("three@sbb.ch").build();
    doReturn(Map.of()).when(userManualMailOverrideServiceMock).getMailsByUserIds(List.of("u333333"));

    // When
    UserModel enriched = userManualMailEnricher.enrich(user);

    // Then
    assertThat(enriched.getMail()).isEqualTo("three@sbb.ch");
    assertThat(enriched.getOriginalMail()).isEqualTo("three@sbb.ch");
  }

}
