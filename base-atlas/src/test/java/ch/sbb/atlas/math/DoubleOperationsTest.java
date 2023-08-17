package ch.sbb.atlas.math;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DoubleOperationsTest {

  @Test
  void shouldRoundToElevenDigits() {
    assertThat(DoubleOperations.round(1.1234567890123, 11)).isEqualTo(1.12345678901);
  }

  @Test
  void shouldRoundUpToElevenDigits() {
    assertThat(DoubleOperations.round(1.1234567890173, 11)).isEqualTo(1.12345678902);
  }
}