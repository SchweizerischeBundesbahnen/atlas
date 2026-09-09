package ch.sbb.atlas.versioning.helper;

import static java.util.Objects.isNull;

import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
class DateHelper {

  boolean areDatesSequential(LocalDate current, LocalDate next) {
    if (isNull(current)) {
      throw new VersioningException("Current date is null");
    }
    if (isNull(next)) {
      throw new VersioningException("Next date is null");
    }
    return current.plusDays(1).equals(next);
  }

}
