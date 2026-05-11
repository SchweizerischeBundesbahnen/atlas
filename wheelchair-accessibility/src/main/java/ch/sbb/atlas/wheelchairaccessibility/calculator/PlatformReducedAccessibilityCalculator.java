package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;

public class PlatformReducedAccessibilityCalculator {

  public WheelchairAccessibilityState calculate(AccessibilityPlatform platformVersion) {
    if (platformVersion.getShuttle() == BooleanOptionalAttributeType.YES) {
      return WheelchairAccessibilityState.SHUTTLE;
    }
    return mapVehicleAccess(platformVersion.getVehicleAccess());
  }

  private static WheelchairAccessibilityState mapVehicleAccess(VehicleAccessAttributeType vehicleAccess) {
    if (vehicleAccess == VehicleAccessAttributeType.TO_BE_COMPLETED) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    return WheelchairAccessibilityState.of(vehicleAccess.getRank());
  }
}
