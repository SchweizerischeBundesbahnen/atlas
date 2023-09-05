package ch.sbb.exportservice.reader;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportType;
import java.time.LocalDate;

public class SqlQueryUtil {

  public static String getFromStatementQueryForWorldOnlyTypes(ExportType exportType, String fromStatement) {
    LocalDate exportDate = LocalDate.now();
    if (exportType.equals(ExportType.WORLD_ONLY_TIMETABLE_FUTURE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(fromStatement, DateHelper.getDateAsSqlString(exportDate), DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClauseForWorldOnlyTypes(ExportType exportType, String whereStatement) {
    if (exportType.equals(ExportType.WORLD_FULL)) {
      return "";
    }
    LocalDate exportDate = LocalDate.now();
    if (exportType.equals(ExportType.WORLD_ONLY_TIMETABLE_FUTURE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(whereStatement, DateHelper.getDateAsSqlString(exportDate));
  }

}
