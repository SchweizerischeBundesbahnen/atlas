package ch.sbb.atlas.model;

import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@ToString
@Setter
public class DateRange {

  private LocalDate from;
  private LocalDate to;

  public boolean canMergeWith(DateRange other) {
    return to.plusDays(1).equals(other.getFrom()) || from.minusDays(1).equals(other.getTo());
  }

  public DateRange mergeWith(DateRange other) {
    return new DateRange(DateHelper.min(from, other.getFrom()), DateHelper.max(to, other.getTo()));
  }

  public boolean contains(LocalDate localDate) {
    return !localDate.isBefore(from) && !localDate.isAfter(to);
  }

  public boolean containsToday() {
    return contains(LocalDate.now());
  }

  public boolean overlapsWith(DateRange dateRange) {
    return !to.isBefore(dateRange.getFrom()) && !from.isAfter(dateRange.getTo());
  }

  public boolean containsEveryDateOf(DateRange otherDateRange) {
    return !from.isAfter(otherDateRange.getFrom()) && !to.isBefore(otherDateRange.getTo());
  }

  public boolean isDateRangeContainedIn(DateRange givenDateRange) {
    return (from.equals(givenDateRange.getFrom()) || from.isAfter(givenDateRange.getFrom()))
        && (to.equals(givenDateRange.getTo()) || to.isBefore(givenDateRange.getTo()));
  }

  public long getValidityInDays() {
    return ChronoUnit.DAYS.between(from, to) + 1;
  }

  public static DateRange fromVersionable(Versionable versionable) {
    return DateRange.builder().from(versionable.getValidFrom()).to(versionable.getValidTo()).build();
  }

}
