package ch.sbb.atlas.versioning.date;

import static java.util.Objects.isNull;

import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;

public final class DateHelper {

  private DateHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean areDatesSequential(LocalDate current, LocalDate next) {
    if (isNull(current)) {
      throw new VersioningException("Current date is null");
    }
    if (isNull(next)) {
      throw new VersioningException("Next date is null");
    }
    return current.plusDays(1).equals(next);
  }

  public static LocalDate min(LocalDate date1, LocalDate date2) {
    if (date1.isBefore(date2)) {
      return date1;
    }
    return date2;
  }
}
