package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import java.util.Set;

final class StopPointCompleteAccessibilityCalculator {

  private static final Set<StandardAttributeType> YES_OR_PARTIALLY = Set.of(
      StandardAttributeType.YES,
      StandardAttributeType.PARTIALLY
  );

  private StopPointCompleteAccessibilityCalculator() {
  }

  static WheelchairAccessibilityState calculate(AccessibilityStopPoint stopPoint) {

    if (YES_OR_PARTIALLY.contains(stopPoint.getAlternativeTransport())) {
      return WheelchairAccessibilityState.SHUTTLE;
    }

    if (YES_OR_PARTIALLY.contains(stopPoint.getAssistanceService())) {
      if (YES_OR_PARTIALLY.contains(stopPoint.getAssistanceAvailability())) {
        return WheelchairAccessibilityState.PRE_REGISTRATION;
      }
      if (stopPoint.getAssistanceAvailability() == StandardAttributeType.NO) {
        return WheelchairAccessibilityState.RAMP_USE;
      }
    }

    if (stopPoint.getAssistanceRequestFulfilled() == BooleanOptionalAttributeType.YES) {
      if (stopPoint.getAssistanceService() == StandardAttributeType.NOT_APPLICABLE) {
        return WheelchairAccessibilityState.PRE_REGISTRATION;
      }
    }

    if (stopPoint.getAssistanceRequestFulfilled() == BooleanOptionalAttributeType.NO
        || stopPoint.getAssistanceService() == StandardAttributeType.NO) {
      return WheelchairAccessibilityState.NO_ACCESS;
    }

    return WheelchairAccessibilityState.NO_INFO;
  }
}
