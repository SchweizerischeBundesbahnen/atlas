package ch.sbb.atlas.api.prm.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BooleanOptionalAttributeTypeTest {

  @Test
  void shouldGetTypeFromValue() {
    assertThat(BooleanOptionalAttributeType.of(0)).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(BooleanOptionalAttributeType.of(1)).isEqualTo(BooleanOptionalAttributeType.YES);
    assertThat(BooleanOptionalAttributeType.of(2)).isEqualTo(BooleanOptionalAttributeType.NO);
    assertThat(BooleanOptionalAttributeType.of(null)).isNull();
  }

}