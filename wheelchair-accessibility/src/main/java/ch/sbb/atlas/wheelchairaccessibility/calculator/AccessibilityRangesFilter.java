package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRanges;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
class AccessibilityRangesFilter {

  private final AccessibilityFilter accessibilityFilter;

  AccessibilityRanges applyTo(AccessibilityRanges accessibilityRanges) {
    return new AccessibilityRanges(applyTo(accessibilityRanges.getDateRanges()));
  }

  List<DateRange> applyTo(List<DateRange> accessibilityRanges) {
    LocalDate filterEnd = accessibilityFilter.getFrom().plusDays(accessibilityFilter.getDays());
    DateRange filterRange = DateRange.builder()
        .from(accessibilityFilter.getFrom())
        .to(filterEnd)
        .build();

    List<DateRange> filteredRanges = new ArrayList<>(
        accessibilityRanges.stream().filter(i -> i.overlapsWith(filterRange)).toList());
    if (filteredRanges.isEmpty()) {
      return filteredRanges;
    }

    if (filterRange.getFrom().isBefore(filteredRanges.getFirst().getFrom())) {
      filteredRanges.addFirst(new DateRange(filterRange.getFrom(), filteredRanges.getFirst().getFrom().minusDays(1)));
    }
    if (filterRange.getTo().isAfter(filteredRanges.getLast().getTo())) {
      filteredRanges.addLast(new DateRange(filteredRanges.getLast().getTo().plusDays(1), filterRange.getTo()));
    }

    filteredRanges.getFirst().setFrom(accessibilityFilter.getFrom());
    filteredRanges.getLast().setTo(filterEnd);
    return filteredRanges;
  }

}
