package ch.sbb.atlas.api.prm.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BoardingDeviceAttributeTypeTest {

  @Test
  void shouldGetTypeFromValue() {
    assertThat(BoardingDeviceAttributeType.of(0)).isEqualTo(BoardingDeviceAttributeType.TO_BE_COMPLETED);
    assertThat(BoardingDeviceAttributeType.of(2)).isEqualTo(BoardingDeviceAttributeType.NO);
    assertThat(BoardingDeviceAttributeType.of(3)).isEqualTo(BoardingDeviceAttributeType.NOT_APPLICABLE);
    assertThat(BoardingDeviceAttributeType.of(null)).isNull();
  }

}