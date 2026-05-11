package ch.sbb.prm.directory.module.wheelchairaccessibility.helper;

import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;

public final class ValidityHelper {
  public static boolean isValidToday(LocalDate validFrom, LocalDate validTo) {
    return new DateRange(validFrom, validTo).containsToday();
  }

}
