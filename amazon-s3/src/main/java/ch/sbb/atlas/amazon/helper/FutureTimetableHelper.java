package ch.sbb.atlas.amazon.helper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import lombok.experimental.UtilityClass;

/**
 * To check which day of the year should be the Actual Future Timetable
 * see <a href="https://www.fahrplanfelder.ch/en/explanations/timetable-year.html">Timetable year</a>
 */
@UtilityClass
public class FutureTimetableHelper {

  public static final int DECEMBER_MONTH_AS_NUMER = 12;

  public static LocalDate getFutureTimetableDate(LocalDate localDate) {
    LocalDate actualFutureTimetableDate = getActualFutureTimetableDate(localDate);
    if (localDate.isBefore(actualFutureTimetableDate)) {
      return actualFutureTimetableDate;
    }
    return getActualFutureTimetableDate(localDate.plusYears(1));
  }

  public static LocalDate getActualFutureTimetableDate(LocalDate localDate) {
    return localDate
        .withMonth(DECEMBER_MONTH_AS_NUMER)
        .with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.SATURDAY))
        .plusDays(1);
  }

}
