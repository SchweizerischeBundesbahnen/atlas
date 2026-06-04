package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;

class PlatformWheelchairAccessibilityCalculator extends WheelchairAccessibilityCalculator {

  @Override
  WheelchairAccessibilityState calculateOnDate(AccessibilityRequest accessibilityRequest) {
    if (accessibilityRequest.getStopPoint().size() != 1 || accessibilityRequest.getPlatform().size() != 1) {
      return WheelchairAccessibilityState.NO_INFO;
    }

    AccessibilityStopPoint accessibilityStopPoint = accessibilityRequest.getStopPoint().getFirst();
    AccessibilityPlatform platform = accessibilityRequest.getPlatform().getFirst();

    if (accessibilityStopPoint.isReduced()) {
      return PlatformReducedAccessibilityCalculator.calculate(platform);
    }

    WheelchairAccessibilityState platformState = PlatformCompleteAccessibilityCalculator.calculate(platform,
        accessibilityRequest.getRelationsOfPlatform(platform.getSloid()));
    WheelchairAccessibilityState stopPointState = StopPointCompleteAccessibilityCalculator.calculate(accessibilityStopPoint);
    return WheelchairAccessibilityCombiner.combine(stopPointState, platformState);
  }

}
