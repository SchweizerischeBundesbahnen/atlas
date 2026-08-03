package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class UserModelTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldReturnManualMailAsEffectiveMailWhenManualMailIsSet() {
    // Given
    UserModel user = UserModel.builder()
        .mail("azure@sbb.ch")
        .manualMailOverride("manual@sbb.ch")
        .build();

    // When
    String effectiveMail = user.getEffectiveMail();

    // Then
    assertThat(effectiveMail).isEqualTo("manual@sbb.ch");
  }

  @Test
  void shouldReturnAzureMailAsEffectiveMailWhenManualMailIsNull() {
    // Given
    UserModel user = UserModel.builder()
        .mail("azure@sbb.ch")
        .manualMailOverride(null)
        .build();

    // When
    String effectiveMail = user.getEffectiveMail();

    // Then
    assertThat(effectiveMail).isEqualTo("azure@sbb.ch");
  }

  @Test
  void shouldReturnAzureMailAsEffectiveMailWhenManualMailIsBlank() {
    // Given
    UserModel user = UserModel.builder()
        .mail("azure@sbb.ch")
        .manualMailOverride("   ")
        .build();

    // When
    String effectiveMail = user.getEffectiveMail();

    // Then
    assertThat(effectiveMail).isEqualTo("azure@sbb.ch");
  }

  @Test
  void shouldReturnNullAsEffectiveMailWhenNeitherMailIsSet() {
    // Given
    UserModel user = UserModel.builder()
        .mail(null)
        .manualMailOverride(null)
        .build();

    // When
    String effectiveMail = user.getEffectiveMail();

    // Then
    assertThat(effectiveMail).isNull();
  }

  @Test
  void shouldNotSerializeEffectiveMail() {
    // Given
    UserModel user = UserModel.builder()
        .sbbUserId("u111111")
        .mail("azure@sbb.ch")
        .manualMailOverride("manual@sbb.ch")
        .build();

    // When
    String json = objectMapper.writeValueAsString(user);

    // Then
    assertThat(json)
        .contains("\"mail\"", "\"manualMailOverride\"")
        .doesNotContain("effectiveMail");
  }

}
