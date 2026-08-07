package ch.sbb.atlas.api.user.administration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class UserModelTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldSerializeBothMailAndOriginalMail() {
    // Given
    UserModel user = UserModel.builder()
        .sbbUserId("u111111")
        .mail("manual@sbb.ch")
        .originalMail("original@sbb.ch")
        .build();

    // When
    String json = objectMapper.writeValueAsString(user);

    // Then
    assertThat(json).contains("\"mail\":\"manual@sbb.ch\"", "\"originalMail\":\"original@sbb.ch\"");
  }

  @Test
  void shouldExposeOriginalMailAsSeparateFieldWhenOverrideIsSet() {
    // Given
    UserModel user = UserModel.builder()
        .mail("manual@sbb.ch")
        .originalMail("original@sbb.ch")
        .build();

    // Then
    assertThat(user.getMail()).isEqualTo("manual@sbb.ch");
    assertThat(user.getOriginalMail()).isEqualTo("original@sbb.ch");
    assertThat(user.getMail()).isNotEqualTo(user.getOriginalMail());
  }

  @Test
  void shouldHaveEqualMailAndOriginalMailWhenNoOverrideIsSet() {
    // Given
    UserModel user = UserModel.builder()
        .mail("original@sbb.ch")
        .originalMail("original@sbb.ch")
        .build();

    // Then
    assertThat(user.getMail()).isEqualTo(user.getOriginalMail());
  }

}
