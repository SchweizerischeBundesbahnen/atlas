package ch.sbb.atlas.wheelchairaccessibility.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlatformReducedAccessibilityCalculatorTest {

  private final PlatformReducedAccessibilityCalculator calculator = new PlatformReducedAccessibilityCalculator();

  @Test
  void shouldReturnShuttleWhenShuttleIsYes() {
    AccessibilityPlatform platform = AccessibilityPlatform.builder()
        .shuttle(BooleanOptionalAttributeType.YES)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE)
        .build();

    WheelchairAccessibilityState result = calculator.calculate(platform);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.SHUTTLE);
  }

  @ParameterizedTest
  @CsvSource({
      "PLATFORM_ACCESS_WITHOUT_ASSISTANCE,            AUTONOMY",
      "PLATFORM_ACCESS_WITH_ASSISTANCE,               RAMP_USE",
      "PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED, PRE_REGISTRATION",
      "PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE,            NO_ACCESS",
      "TO_BE_COMPLETED,                               NO_INFO"
  })
  void shouldMapVehicleAccessToStateWhenShuttleIsNo(VehicleAccessAttributeType vehicleAccess,
      WheelchairAccessibilityState expected) {
    AccessibilityPlatform platform = AccessibilityPlatform.builder()
        .shuttle(BooleanOptionalAttributeType.NO)
        .vehicleAccess(vehicleAccess)
        .build();

    WheelchairAccessibilityState result = calculator.calculate(platform);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void shouldMapVehicleAccessWhenShuttleIsToBeCompleted() {
    AccessibilityPlatform platform = AccessibilityPlatform.builder()
        .shuttle(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .vehicleAccess(VehicleAccessAttributeType.PLATFORM_ACCESS_WITHOUT_ASSISTANCE)
        .build();

    WheelchairAccessibilityState result = calculator.calculate(platform);

    assertThat(result).isEqualTo(WheelchairAccessibilityState.AUTONOMY);
  }
}
