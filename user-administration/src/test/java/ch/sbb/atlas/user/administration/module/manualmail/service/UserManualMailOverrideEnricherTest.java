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
  private UserManualMailService userManualMailServiceMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userManualMailEnricher = new UserManualMailEnricher(userManualMailServiceMock);
  }

  @Test
  void shouldEnrichUserPageWithManualMailsInSingleQuery() {
    // Given
    UserModel user1 = UserModel.builder().sbbUserId("u111111").mail("one@sbb.ch").build();
    UserModel user2 = UserModel.builder().sbbUserId("u222222").mail("two@sbb.ch").build();
    doReturn(Map.of("u111111", "manual-one@sbb.ch")).when(userManualMailServiceMock)
        .getMailsByUserIds(List.of("u111111", "u222222"));

    // When
    List<UserModel> enriched = userManualMailEnricher.enrich(List.of(user1, user2));

    // Then
    assertThat(enriched).extracting(UserModel::getManualMailOverride).containsExactly("manual-one@sbb.ch", null);
  }

  @Test
  void shouldLeaveManualMailNullWhenNoOverrideExists() {
    // Given
    UserModel user = UserModel.builder().sbbUserId("u333333").mail("three@sbb.ch").build();
    doReturn(Map.of()).when(userManualMailServiceMock).getMailsByUserIds(List.of("u333333"));

    // When
    UserModel enriched = userManualMailEnricher.enrich(user);

    // Then
    assertThat(enriched.getManualMailOverride()).isNull();
  }

}
