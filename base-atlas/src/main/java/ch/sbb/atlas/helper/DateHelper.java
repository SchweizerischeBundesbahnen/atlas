package ch.sbb.atlas.helper;

import ch.sbb.atlas.api.AtlasApiConstants;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateHelper {

  public static final DateTimeFormatter DATE_FORMATTER_BASE = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN);

  private DateHelper() {
    throw new IllegalStateException("Utility class");
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
    return localDate.format(DATE_FORMATTER_BASE);
  }

}
