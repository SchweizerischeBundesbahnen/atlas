package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.combiner.WheelchairAccessibilityCombiner;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelation;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import ch.sbb.atlas.wheelchairaccessibility.model.PlatformWithRelations;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class WheelchairAccessibilityCalculator {

  private WheelchairAccessibilityCalculator() {
  }

  public static WheelchairAccessibilityState calculateForPlatform(AccessibilityPlatform platform,
      AccessibilityStopPoint stopPoint,
      List<? extends AccessibilityRelation> relations) {
    if (stopPoint.isReduced()) {
      return PlatformReducedAccessibilityCalculator.calculate(platform);
    }
    WheelchairAccessibilityState platformState = PlatformCompleteAccessibilityCalculator.calculate(platform, relations);
    WheelchairAccessibilityState stopPointState = StopPointCompleteAccessibilityCalculator.calculate(stopPoint);
    return WheelchairAccessibilityCombiner.combine(stopPointState, platformState);
  }

  public static WheelchairAccessibilityState calculateForStopPoint(AccessibilityStopPoint stopPoint,
      Collection<PlatformWithRelations> platforms) {
    if (platforms.isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    return platforms.stream()
        .map(platform -> calculateForPlatform(platform.getPlatform(), stopPoint, platform.getRelations()))
        .max(Comparator.comparingInt(WheelchairAccessibilityState::getRank))
        .orElse(WheelchairAccessibilityState.NO_INFO);
  }

}
