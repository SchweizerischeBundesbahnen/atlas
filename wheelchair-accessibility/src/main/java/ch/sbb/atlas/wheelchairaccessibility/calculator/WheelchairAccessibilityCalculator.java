package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRanges;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class WheelchairAccessibilityCalculator {

  Accessibility calculate(AccessibilityRequest accessibilityRequest, AccessibilityFilter accessibilityFilter) {
    List<DateRange> allDateRanges = accessibilityRequest.getAllDateRanges();

    AccessibilityRanges accessibilityRanges = AccessibilityRangesCalculator.getAccessibilityRanges(allDateRanges);
    log.debug("All AccessibilityRanges: {}", accessibilityRanges);

    AccessibilityRangesFilter accessibilityRangesFilter = new AccessibilityRangesFilter(accessibilityFilter);
    AccessibilityRanges filteredRanges = accessibilityRangesFilter.applyTo(accessibilityRanges);
    log.debug("Filtered by {} AccessibilityRanges: {}", accessibilityRangesFilter, filteredRanges);

    Accessibility accessibility = new Accessibility();
    for (DateRange dateRange : filteredRanges) {
      AccessibilityRequest accessibilityRequestOnDate = accessibilityRequest.getRequestOnDate(dateRange.getFrom());
      accessibility.with(dateRange, calculateOnDate(accessibilityRequestOnDate));
    }

    log.debug("Calculated accessibility: {}", accessibility);
    return accessibility;
  }

  abstract WheelchairAccessibilityState calculateOnDate(AccessibilityRequest accessibilityRequest);
}
