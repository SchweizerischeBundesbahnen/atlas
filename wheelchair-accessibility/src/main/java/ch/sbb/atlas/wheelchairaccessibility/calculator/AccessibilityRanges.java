package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
class AccessibilityRanges {

  static List<DateRange> getAccessibilityRanges(List<DateRange> versionRanges) {
    List<DateRange> accessibilityRanges = new ArrayList<>();

    LocalDate current = VersioningData.MIN_DATE;
    do {
      LocalDate next = getNext(versionRanges, current);
      LocalDate nextRangeEnd = next.equals(VersioningData.MAX_DATE) ? VersioningData.MAX_DATE : next.minusDays(1);

      accessibilityRanges.add(new DateRange(current, nextRangeEnd));

      current = next;
    } while (current.isBefore(VersioningData.MAX_DATE));

    return accessibilityRanges;
  }

  private static LocalDate getNext(List<DateRange> dateRanges, LocalDate current) {
    LocalDate nextValidFrom = dateRanges.stream()
        .map(DateRange::getFrom)
        .filter(i -> i.isAfter(current))
        .min(LocalDate::compareTo)
        .orElse(VersioningData.MAX_DATE);

    LocalDate nextValidTo = dateRanges.stream()
        .map(DateRange::getTo)
        .filter(i -> i.isAfter(current))
        .min(LocalDate::compareTo)
        .map(i -> i.plusDays(1))
        .orElse(VersioningData.MAX_DATE);
    return nextValidFrom.isBefore(nextValidTo) ? nextValidFrom : nextValidTo;
  }

}
