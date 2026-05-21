package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.VersioningData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import ch.sbb.atlas.wheelchairaccessibility.model.PlatformAccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.PlatformWithRelations;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WheelchairAccessibilityCalculator {

  public static Map<DateRange, WheelchairAccessibilityState> calculateForPlatform(
      PlatformAccessibilityRequest platformAccessibilityRequest) {

    List<DateRange> allDateRanges = platformAccessibilityRequest.getAllDateRanges();
    if (allDateRanges.isEmpty()) {
      return Map.of(new DateRange(VersioningData.MIN_DATE, VersioningData.MAX_DATE), WheelchairAccessibilityState.NO_INFO);
    }

    List<DateRange> accessibilityRanges = AccessibilityRanges.getAccessibilityRanges(allDateRanges);
    Map<DateRange, WheelchairAccessibilityState> result = new HashMap<>();
    for (DateRange dateRange : accessibilityRanges) {
      // TODO: calculate info on dateRange.From, modify calculation/interface to get it for a day
      result.put(dateRange, WheelchairAccessibilityState.NO_INFO);
    }

    return result;
  }

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
