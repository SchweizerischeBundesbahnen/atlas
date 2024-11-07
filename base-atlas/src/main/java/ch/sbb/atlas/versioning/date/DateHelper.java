package ch.sbb.atlas.versioning.date;

import static java.util.Objects.isNull;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

  public static LocalDate min(LocalDate x, LocalDate y) {
    if (x.isBefore(y)) {
      return x;
    }
    return y;
  }

  public static LocalDate max(LocalDate x, LocalDate y) {
    if (x.isAfter(y)) {
      return x;
    }
    return y;
  }

  public static String getDateAsSqlString(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }

}
