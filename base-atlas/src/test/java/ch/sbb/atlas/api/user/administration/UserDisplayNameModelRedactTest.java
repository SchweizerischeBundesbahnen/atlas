package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.redact.RedactAspect;
import org.junit.jupiter.api.Test;

class UserDisplayNameModelRedactTest {

  @Test
  void shouldRedactDisplayNameButKeepSbbUserId() {
    // Given
    UserDisplayNameModel clientAlias = UserDisplayNameModel.builder()
        .sbbUserId("client-id")
        .displayName("PostAuto")
        .build();

    // When
    UserDisplayNameModel redacted = (UserDisplayNameModel) RedactAspect.redactResult(clientAlias);

    // Then
    assertThat(redacted.getSbbUserId()).isEqualTo("client-id");
    assertThat(redacted.getDisplayName()).isEqualTo("*****");
  }

  @Test
  void shouldNotModifyOriginalObjectWhenRedacting() {
    // Given
    UserDisplayNameModel clientAlias = UserDisplayNameModel.builder()
        .sbbUserId("client-id")
        .displayName("PostAuto")
        .build();

    // When
    RedactAspect.redactResult(clientAlias);

    // Then
    assertThat(clientAlias.getDisplayName()).isEqualTo("PostAuto");
  }

  @Test
  void shouldKeepNullDisplayNameWhenRedacting() {
    // Given
    UserDisplayNameModel unknownUser = UserDisplayNameModel.builder()
        .sbbUserId("ATLAS_SYSTEM_USER")
        .build();

    // When
    UserDisplayNameModel redacted = (UserDisplayNameModel) RedactAspect.redactResult(unknownUser);

    // Then
    assertThat(redacted.getDisplayName()).isNull();
  }
}
