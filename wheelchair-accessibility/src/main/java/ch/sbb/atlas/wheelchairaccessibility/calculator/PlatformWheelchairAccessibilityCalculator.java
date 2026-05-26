package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRanges;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PlatformWheelchairAccessibilityCalculator {

  public static Accessibility calculate(AccessibilityRequest accessibilityRequest, AccessibilityFilter accessibilityFilter) {
    List<DateRange> allDateRanges = accessibilityRequest.getAllDateRanges();

    AccessibilityRanges accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(allDateRanges);
    log.info("All AccessibilityRanges: {}", accessibilityRanges);

    AccessibilityRangesFilter accessibilityRangesFilter = new AccessibilityRangesFilter(accessibilityFilter);
    AccessibilityRanges filteredRanges = accessibilityRangesFilter.applyTo(accessibilityRanges);
    log.info("Filtered by {} AccessibilityRanges: {}", accessibilityRangesFilter, filteredRanges);

    Accessibility accessibility = new Accessibility();
    for (DateRange dateRange : filteredRanges) {
      AccessibilityRequest accessibilityRequestOnDate = accessibilityRequest.getRequestOnDate(dateRange.getFrom());
      accessibility.with(dateRange, calculateOnDate(accessibilityRequestOnDate));
    }

    log.info("Calculated accessibility: {}", accessibility);
    return accessibility;
  }

  public static WheelchairAccessibilityState calculateOnDate(AccessibilityRequest accessibilityRequest) {
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

}
