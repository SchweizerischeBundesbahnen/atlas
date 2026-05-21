package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import ch.sbb.atlas.wheelchairaccessibility.model.PlatformWithRelations;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WheelchairAccessibilityCalculator {

  public static WheelchairAccessibilityState calculateForPlatform(AccessibilityStopPoint stopPoint,
      PlatformWithRelations platformWithRelations) {
    if (stopPoint.isReduced()) {
      return PlatformReducedAccessibilityCalculator.calculate(platformWithRelations.getPlatform());
    }
    WheelchairAccessibilityState platformState =
        PlatformCompleteAccessibilityCalculator.calculate(platformWithRelations.getPlatform(),
            platformWithRelations.getRelations());
    WheelchairAccessibilityState stopPointState = StopPointCompleteAccessibilityCalculator.calculate(stopPoint);
    return WheelchairAccessibilityCombiner.combine(stopPointState, platformState);
  }

  public static WheelchairAccessibilityState calculateForStopPoint(AccessibilityStopPoint stopPoint,
      List<PlatformWithRelations> platforms) {
    if (platforms.isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    return platforms.stream()
        .map(platformWithRelations -> calculateForPlatform(stopPoint, platformWithRelations))
        .max(Comparator.comparingInt(WheelchairAccessibilityState::getRank))
        .orElse(WheelchairAccessibilityState.NO_INFO);
  }

}
