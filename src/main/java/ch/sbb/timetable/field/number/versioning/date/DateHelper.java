package ch.sbb.timetable.field.number.versioning.date;

import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.util.Objects;

public final class DateHelper {

  private DateHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean areDatesSequential(LocalDate current, LocalDate next) {
    if (Objects.isNull(current)) {
      throw new VersioningException("Current date is null");
    }
    if (Objects.isNull(next)) {
      throw new VersioningException("Next date is null");
    }
    return current.plusDays(1).equals(next);
  }

}
