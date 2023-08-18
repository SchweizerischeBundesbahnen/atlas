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

  @Test
  void shouldGetFractionsOfDouble() {
    assertThat(DoubleOperations.getFractions(1.1234567890173)).isEqualTo(13);
    assertThat(DoubleOperations.getFractions(1.2)).isEqualTo(1);
    assertThat(DoubleOperations.getFractions(1.232)).isEqualTo(3);
  }
}