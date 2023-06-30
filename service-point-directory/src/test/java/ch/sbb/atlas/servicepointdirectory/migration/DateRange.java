package ch.sbb.atlas.servicepointdirectory.migration;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DateRange {

  private LocalDate from;
  private LocalDate to;

  public boolean canMergeWith(DateRange other) {
    return to.plusDays(1).equals(other.getFrom()) || from.minusDays(1).equals(other.getTo());
  }

  public DateRange mergeWith(DateRange other) {
    return new DateRange(getMin(from, other.getFrom()), getMax(to, other.getTo()));
  }

  private LocalDate getMin(LocalDate x, LocalDate y) {
    if (x.isBefore(y)) {
      return x;
    }
    return y;
  }

  private LocalDate getMax(LocalDate x, LocalDate y) {
    if (x.isAfter(y)) {
      return x;
    }
    return y;
  }
}
