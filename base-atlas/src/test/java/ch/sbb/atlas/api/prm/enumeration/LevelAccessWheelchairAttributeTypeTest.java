package ch.sbb.atlas.api.prm.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LevelAccessWheelchairAttributeTypeTest {

  @Test
  void shouldGetTypeFromValue() {
    LevelAccessWheelchairAttributeType levelAccessWheelchairAttributeType = LevelAccessWheelchairAttributeType.of(0);
    assertThat(levelAccessWheelchairAttributeType).isEqualTo(LevelAccessWheelchairAttributeType.TO_BE_COMPLETED);

    assertThat(BasicAttributeType.of(null)).isNull();
  }

}
