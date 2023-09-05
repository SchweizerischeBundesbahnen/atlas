package ch.sbb.atlas.servicepointdirectory.migration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class Validity {

  private List<DateRange> dateRanges;

  public boolean isNotOverlapping() {
    dateRanges.sort(Comparator.comparing(DateRange::getFrom));

    List<LocalDate> markesOfRanges = new ArrayList<>();
    dateRanges.forEach(dateRange -> {
      markesOfRanges.add(dateRange.getFrom());
      if (!dateRange.getFrom().equals(dateRange.getTo())) {
        markesOfRanges.add(dateRange.getTo());
      }
    });
    return areDatesSequential(markesOfRanges);
  }

  private boolean areDatesSequential(List<LocalDate> markesOfRanges) {
    Iterator<LocalDate> iter = markesOfRanges.iterator();
    LocalDate current, previous = iter.next();
    while (iter.hasNext()) {
      current = iter.next();
      if (!previous.isBefore(current)) {
        return false;
      }
      previous = current;
    }
    return true;
  }

  public Validity minify() {
    if (isNotOverlapping()) {
      return minifyNotOverlappingSortedRanges();
    }
    throw new IllegalStateException("Could not minify, because there are overlaps");
  }

  private Validity minifyNotOverlappingSortedRanges() {
    List<DateRange> minifiedRanges = new ArrayList<>();
    minifiedRanges.add(dateRanges.get(0));

    Iterator<DateRange> iter = dateRanges.iterator();
    DateRange current, previous = iter.next();
    while (iter.hasNext()) {
      current = iter.next();
      if (previous.canMergeWith(current)) {
        // Remove last element
        DateRange removed = minifiedRanges.remove(minifiedRanges.size() - 1);
        // Replace it with merged
        minifiedRanges.add(removed.mergeWith(current));
      } else {
        minifiedRanges.add(current);
      }
      previous = current;
    }

    return new Validity(minifiedRanges);
  }

}
