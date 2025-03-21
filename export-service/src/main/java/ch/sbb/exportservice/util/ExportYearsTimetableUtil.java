package ch.sbb.exportservice.util;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.FutureTimetableHelper;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExportYearsTimetableUtil {

  public static DateRange getTimetableYearsDateRange() {
    return getTimetableYearsDateRange(LocalDate.now());
  }

  static DateRange getTimetableYearsDateRange(LocalDate referenceDate) {
    LocalDate yearsStart = FutureTimetableHelper.getTimetableYearChangeDateToExportData(referenceDate.minusYears(1));
    LocalDate yearsEnd = FutureTimetableHelper.getTimetableYearChangeDateToExportData(referenceDate.plusYears(1)).minusDays(1);
    return new DateRange(yearsStart, yearsEnd);
  }

}
