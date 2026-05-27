package ch.sbb.atlas.wheelchairaccessibility.calculator;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.VersioningData;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRanges;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
class AccessibilityRangesCalculator {

  static AccessibilityRanges getAccessibilityRanges(List<DateRange> versionRanges) {
    if (versionRanges.isEmpty()) {
      return new AccessibilityRanges(List.of(new DateRange(VersioningData.MIN_DATE, VersioningData.MAX_DATE)));
    }

    List<DateRange> accessibilityRanges = new ArrayList<>();

    Optional<LocalDate> current = getNext(versionRanges, VersioningData.MIN_DATE);

    while (current.isPresent()) {
      Optional<LocalDate> next = getNext(versionRanges, current.get());
      if (next.isEmpty()) {
        break;
      }

      LocalDate nextRangeEnd = next.get().equals(VersioningData.MAX_DATE)
          ? VersioningData.MAX_DATE
          : next.get().minusDays(1);

      accessibilityRanges.add(new DateRange(current.get(), nextRangeEnd));

      current = next;
    }

    return new AccessibilityRanges(accessibilityRanges);
  }

  private static Optional<LocalDate> getNext(List<DateRange> dateRanges, LocalDate current) {
    Optional<LocalDate> nextValidFrom = dateRanges.stream()
        .map(DateRange::getFrom)
        .filter(i -> i.isAfter(current))
        .min(LocalDate::compareTo);

    Optional<LocalDate> nextValidTo = dateRanges.stream()
        .map(DateRange::getTo)
        .filter(i -> !i.isBefore(current))
        .min(LocalDate::compareTo)
        .map(i -> i.plusDays(1));

    if (nextValidTo.isPresent()) {
      if (nextValidFrom.isPresent()) {
        return nextValidFrom.get().isBefore(nextValidTo.get()) ? nextValidFrom : nextValidTo;
      }
      return nextValidTo;
    }
    return Optional.empty();
  }

}
