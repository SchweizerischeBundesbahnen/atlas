package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AccessibilityRangesFilter {

  private final AccessibilityFilter accessibilityFilter;

  List<DateRange> applyTo(List<DateRange> accessibilityRanges) {
    LocalDate filterEnd = accessibilityFilter.getFrom().plusDays(accessibilityFilter.getDays());
    DateRange filterRange = DateRange.builder()
        .from(accessibilityFilter.getFrom())
        .to(filterEnd)
        .build();

    List<DateRange> filteredRanges = accessibilityRanges.stream().filter(i -> i.overlapsWith(filterRange)).toList();
    filteredRanges.getFirst().setFrom(accessibilityFilter.getFrom());
    filteredRanges.getLast().setTo(filterEnd);
    return filteredRanges;
  }

}
