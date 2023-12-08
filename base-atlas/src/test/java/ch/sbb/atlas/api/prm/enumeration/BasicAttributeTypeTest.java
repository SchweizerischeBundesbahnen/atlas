package ch.sbb.atlas.api.prm.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BasicAttributeTypeTest {

  @Test
  void shouldGetTypeFromValue() {
    BasicAttributeType basicAttributeType = BasicAttributeType.of(0);
    assertThat(basicAttributeType).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);

    assertThat(BasicAttributeType.of(null)).isNull();
  }
}