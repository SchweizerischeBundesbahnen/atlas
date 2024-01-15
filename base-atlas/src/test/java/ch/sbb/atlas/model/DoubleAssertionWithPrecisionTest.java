package ch.sbb.atlas.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DoubleAssertionWithPrecisionTest {

  @Test
  void shouldBeEqual() {
    assertThat(0.0001).isCloseTo(0.000199, DoubleAssertion.equalOnDecimalDigits(4));

  }

  @Test
  void shouldNotBeEqual() {
    assertThat(0.0001).isNotCloseTo(0.000201, DoubleAssertion.equalOnDecimalDigits(4));
  }

}
