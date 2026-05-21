package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelation;
import java.util.List;
import java.util.Set;

public final class PlatformCompleteAccessibilityCalculator {

  private static final Set<StepFreeAccessAttributeType> STEP_FREE_ACCESS_VALID_VALUES = Set.of(
      StepFreeAccessAttributeType.YES,
      StepFreeAccessAttributeType.YES_WITH_LIFT,
      StepFreeAccessAttributeType.YES_WITH_RAMP
  );

  private static final double SUPERELEVATION_LEGAL_LIMIT_MM = 40.0D;

  private PlatformCompleteAccessibilityCalculator() {
  }

  public static WheelchairAccessibilityState calculate(AccessibilityPlatform platform,
      List<? extends AccessibilityRelation> relations) {

    if (platform.getShuttle() == BooleanOptionalAttributeType.YES) {
      return WheelchairAccessibilityState.SHUTTLE;
    }

    if (!hasAtLeastOneStepFreeAccess(relations)) {
      return WheelchairAccessibilityState.NO_ACCESS;
    }

    if (platform.getLevelAccessWheelchair() == LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE) {
      return WheelchairAccessibilityState.RAMP_USE;
    }

    if (isSuperElevationBelowLimit(platform.getSuperelevation())
        && platform.getLevelAccessWheelchair() == LevelAccessWheelchairAttributeType.YES) {
      return WheelchairAccessibilityState.AUTONOMY;
    }

    if (isBoardingDeviceLiftOrRamp(platform.getBoardingDevice())) {
      return WheelchairAccessibilityState.PRE_REGISTRATION;
    }

    if (platform.getLevelAccessWheelchair() == LevelAccessWheelchairAttributeType.NO
        || platform.getBoardingDevice() == BoardingDeviceAttributeType.NO) {
      return WheelchairAccessibilityState.NO_ACCESS;
    }

    return WheelchairAccessibilityState.NO_INFO;
  }

  private static boolean hasAtLeastOneStepFreeAccess(List<? extends AccessibilityRelation> relations) {
    return relations.stream()
        .map(AccessibilityRelation::getStepFreeAccess)
        .anyMatch(STEP_FREE_ACCESS_VALID_VALUES::contains);
  }

  private static boolean isSuperElevationBelowLimit(Double superelevation) {
    return superelevation == null || superelevation < SUPERELEVATION_LEGAL_LIMIT_MM;
  }

  private static boolean isBoardingDeviceLiftOrRamp(BoardingDeviceAttributeType boardingDevice) {
    return boardingDevice == BoardingDeviceAttributeType.LIFTS
        || boardingDevice == BoardingDeviceAttributeType.RAMPS;
  }
}
