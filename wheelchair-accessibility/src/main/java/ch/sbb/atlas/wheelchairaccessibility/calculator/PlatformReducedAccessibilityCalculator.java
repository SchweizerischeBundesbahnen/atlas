package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;

final class PlatformReducedAccessibilityCalculator {

  private PlatformReducedAccessibilityCalculator() {
  }

  static WheelchairAccessibilityState calculate(AccessibilityPlatform platform) {
    if (platform.getShuttle() == BooleanOptionalAttributeType.YES) {
      return WheelchairAccessibilityState.SHUTTLE;
    }
    return mapVehicleAccess(platform.getVehicleAccess());
  }

  private static WheelchairAccessibilityState mapVehicleAccess(VehicleAccessAttributeType vehicleAccess) {
    if (vehicleAccess == VehicleAccessAttributeType.TO_BE_COMPLETED) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    return WheelchairAccessibilityState.of(vehicleAccess.getRank());
  }
}
