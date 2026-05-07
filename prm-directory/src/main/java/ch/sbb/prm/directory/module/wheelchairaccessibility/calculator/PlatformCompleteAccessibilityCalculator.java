package ch.sbb.prm.directory.module.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PlatformCompleteAccessibilityCalculator {

  private static final Set<StepFreeAccessAttributeType> STEP_FREE_ACCESS_VALID_VALUES = Set.of(
      StepFreeAccessAttributeType.YES,
      StepFreeAccessAttributeType.YES_WITH_LIFT,
      StepFreeAccessAttributeType.YES_WITH_RAMP
  );

  private static final double SUPERELEVATION_LEGAL_LIMIT_MM = 40.0D;

  public WheelchairAccessibilityState calculatePlatform(PlatformVersion platformVersion,
      List<RelationVersion> relations) {

    if (platformVersion.getShuttle() == BooleanOptionalAttributeType.YES) {
      return WheelchairAccessibilityState.SHUTTLE;
    }

    if (!hasAtLeastOneStepFreeAccess(relations)) {
      return WheelchairAccessibilityState.NO_ACCESS;
    }

    if (platformVersion.getLevelAccessWheelchair() == LevelAccessWheelchairAttributeType.YES_WITH_STAFF_ASSISTANCE) {
      return WheelchairAccessibilityState.RAMP_USE;
    }

    if (isSuperElevationBelowLimit(platformVersion.getSuperelevation())
        && platformVersion.getLevelAccessWheelchair() == LevelAccessWheelchairAttributeType.YES) {
      return WheelchairAccessibilityState.AUTONOMY;
    }

    if (isBoardingDeviceLiftOrRamp(platformVersion.getBoardingDevice())) {
      return WheelchairAccessibilityState.PRE_REGISTRATION;
    }

    if (platformVersion.getLevelAccessWheelchair() == LevelAccessWheelchairAttributeType.NO
        || platformVersion.getBoardingDevice() == BoardingDeviceAttributeType.NO) {
      return WheelchairAccessibilityState.NO_ACCESS;
    }

    return WheelchairAccessibilityState.NO_INFO;
  }

  private boolean hasAtLeastOneStepFreeAccess(List<RelationVersion> relations) {
    return relations.stream()
        .map(RelationVersion::getStepFreeAccess)
        .anyMatch(STEP_FREE_ACCESS_VALID_VALUES::contains);
  }

  private boolean isSuperElevationBelowLimit(Double superelevation) {
    return superelevation == null || superelevation < SUPERELEVATION_LEGAL_LIMIT_MM;
  }

  private boolean isBoardingDeviceLiftOrRamp(BoardingDeviceAttributeType boardingDevice) {
    return boardingDevice == BoardingDeviceAttributeType.LIFTS
        || boardingDevice == BoardingDeviceAttributeType.RAMPS;
  }
}
