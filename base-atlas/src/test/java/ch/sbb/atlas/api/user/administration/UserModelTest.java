package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

class UserModelTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldReturnManualMailAsEffectiveMailWhenManualMailIsSet() {
    // Given
    UserModel user = UserModel.builder()
        .mail("azure@sbb.ch")
        .manualMail("manual@sbb.ch")
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
        .manualMail(null)
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
        .manualMail("   ")
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
        .manualMail(null)
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
        .manualMail("manual@sbb.ch")
        .build();

    // When
    String json = objectMapper.writeValueAsString(user);

    // Then
    assertThat(json).contains("\"mail\"", "\"manualMail\"");
    assertThat(json).doesNotContain("effectiveMail");
  }

}
