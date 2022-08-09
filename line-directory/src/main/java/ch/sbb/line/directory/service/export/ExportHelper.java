package ch.sbb.line.directory.service.export;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import lombok.experimental.UtilityClass;

/**
 * To check which day of the year should be the Actual Future Timetable
 * see <a href="https://www.fahrplanfelder.ch/en/explanations/timetable-year.html">Timetable year</a>
 */
@UtilityClass
public class ExportHelper {

  public static LocalDate getFutureTimetableDate(LocalDate localDate) {
    LocalDate actualFutureTimetableDate = getActualFutureTimetableDate(localDate);
    if (localDate.isBefore(actualFutureTimetableDate)) {
      return actualFutureTimetableDate;
    }
    return getActualFutureTimetableDate(localDate.plusYears(1));
  }

  static LocalDate getActualFutureTimetableDate(LocalDate localDate) {
    return localDate
        .withMonth(12)
        .with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.SATURDAY))
        .plusDays(1);
  }

}
