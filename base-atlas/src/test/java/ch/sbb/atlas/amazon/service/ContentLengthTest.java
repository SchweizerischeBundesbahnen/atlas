package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ContentLengthTest {

  @Test
  void shouldCreateContentLengthForNonNegativeValue() {
    // When
    ContentLength contentLength = ContentLength.of(42L);

    // Then
    assertThat(contentLength.value()).isEqualTo(42L);
  }

  @Test
  void shouldCreateContentLengthForZero() {
    // When
    ContentLength contentLength = ContentLength.of(0L);

    // Then
    assertThat(contentLength.value()).isZero();
  }

  @Test
  void shouldThrowExceptionForNegativeValue() {
    // When / Then
    assertThatThrownBy(() -> ContentLength.of(-1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must not be negative");
  }

}
