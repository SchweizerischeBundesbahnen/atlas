package ch.sbb.atlas.api.prm.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VehicleAccessAttributeTypeTest {

  @Test
  void shouldGetTypeFromValue() {
    assertThat(VehicleAccessAttributeType.of(0)).isEqualTo(VehicleAccessAttributeType.TO_BE_COMPLETED);
    assertThat(VehicleAccessAttributeType.of(13)).isEqualTo(VehicleAccessAttributeType.PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED);
    assertThat(VehicleAccessAttributeType.of(null)).isNull();
  }

}