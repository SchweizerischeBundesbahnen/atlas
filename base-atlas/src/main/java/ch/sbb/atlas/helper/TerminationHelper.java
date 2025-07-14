package ch.sbb.atlas.helper;

import ch.sbb.atlas.exception.TerminationNotAllowedValidToNotWithinLastVersionRangeException;
import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationHelper {

  public static void isValidToInLastVersionRange(String sloid, DateRange dateRange, LocalDate validTo) {
    if (!dateRange.contains(validTo)) {
      throw new TerminationNotAllowedValidToNotWithinLastVersionRangeException(sloid, validTo, dateRange.getFrom(),
          dateRange.getTo());
    }
  }

}
