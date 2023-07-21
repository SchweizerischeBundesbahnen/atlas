package ch.sbb.atlas.servicepointdirectory.migration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DoubleAssertionWithPrecisionTest {

  @Test
  void shouldBeEqual() {
    assertThat(0.0001).isCloseTo(0.0002, DoubleAssertion.equalOnDecimalDigits(3));

  }

  @Test
  void shouldNotBeEqual() {
    assertThat(0.0001).isNotCloseTo(0.0002, DoubleAssertion.equalOnDecimalDigits(4));
  }
}
