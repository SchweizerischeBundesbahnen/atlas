package ch.sbb.timetable.field.number.versioning.date;

import java.time.LocalDate;

public final class DateHelper {

  private DateHelper(){
    throw new IllegalStateException("Utility class");
  }

  public static boolean areDatesSequential(LocalDate current, LocalDate next){
    return current.plusDays(1).equals(next);
  }

}
