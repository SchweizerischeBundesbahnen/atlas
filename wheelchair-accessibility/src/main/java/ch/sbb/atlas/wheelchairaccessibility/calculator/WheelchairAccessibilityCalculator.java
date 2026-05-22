package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.VersioningData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelation;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WheelchairAccessibilityCalculator {

  public static Map<DateRange, WheelchairAccessibilityState> calculateForPlatform(
      AccessibilityRequest accessibilityRequest, AccessibilityFilter accessibilityFilter) {

    List<DateRange> allDateRanges = accessibilityRequest.getAllDateRanges();
    if (allDateRanges.isEmpty()) {
      return Map.of(new DateRange(VersioningData.MIN_DATE, VersioningData.MAX_DATE), WheelchairAccessibilityState.NO_INFO);
    }

    List<DateRange> accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(allDateRanges);

    AccessibilityRangesFilter accessibilityRangesFilter = new AccessibilityRangesFilter(accessibilityFilter);
    List<DateRange> filteredRanges = accessibilityRangesFilter.applyTo(accessibilityRanges);

    Map<DateRange, WheelchairAccessibilityState> result = new HashMap<>();
    for (DateRange dateRange : filteredRanges) {
      AccessibilityRequest accessibilityRequestOnDate = accessibilityRequest.getRequestOnDate(
          dateRange.getFrom());

      result.put(dateRange, calculateForPlatform(accessibilityRequestOnDate));
    }

    return result;
  }

  public static WheelchairAccessibilityState calculateForPlatform(AccessibilityRequest accessibilityRequest) {
    if (accessibilityRequest.getStopPoint().size() != 1 || accessibilityRequest.getPlatform().size() != 1) {
      return WheelchairAccessibilityState.NO_INFO;
    }

    AccessibilityStopPoint accessibilityStopPoint = accessibilityRequest.getStopPoint().getFirst();
    AccessibilityPlatform platform = accessibilityRequest.getPlatform().getFirst();

    if (accessibilityStopPoint.isReduced()) {
      return PlatformReducedAccessibilityCalculator.calculate(platform);
    }

    WheelchairAccessibilityState platformState = PlatformCompleteAccessibilityCalculator.calculate(platform,
        accessibilityRequest.getRelations());
    WheelchairAccessibilityState stopPointState = StopPointCompleteAccessibilityCalculator.calculate(accessibilityStopPoint);
    return WheelchairAccessibilityCombiner.combine(stopPointState, platformState);
  }

  public static WheelchairAccessibilityState calculateForStopPoint(AccessibilityRequest accessibilityRequest) {
    if (accessibilityRequest.getStopPoint().size() != 1 || accessibilityRequest.getPlatform().isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }

    AccessibilityStopPoint accessibilityStopPoint = accessibilityRequest.getStopPoint().getFirst();

    return accessibilityRequest.getPlatform().stream()
        .map(platform -> calculatePlatformAccessibility(accessibilityRequest, platform, accessibilityStopPoint))
        .max(Comparator.comparingInt(WheelchairAccessibilityState::getRank))
        .orElse(WheelchairAccessibilityState.NO_INFO);
  }

  private static WheelchairAccessibilityState calculatePlatformAccessibility(AccessibilityRequest accessibilityRequest,
      AccessibilityPlatform platform, AccessibilityStopPoint accessibilityStopPoint) {
    List<? extends AccessibilityRelation> relationsOfPlatform = accessibilityRequest.getRelations().stream()
        .filter(i -> i.getSloid().equals(platform.getSloid())).toList();
    AccessibilityRequest plattformAccessibilityRequest = AccessibilityRequest.builder()
        .stopPoint(List.of(accessibilityStopPoint))
        .platform(List.of(platform))
        .relations(relationsOfPlatform)
        .build();

    return calculateForPlatform(plattformAccessibilityRequest);
  }

}
