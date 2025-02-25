package ch.sbb.exportservice.reader;

import static ch.sbb.exportservice.model.ExportType.ACTUAL;
import static ch.sbb.exportservice.model.ExportType.FULL;
import static ch.sbb.exportservice.model.ExportType.FUTURE_TIMETABLE;
import static ch.sbb.exportservice.model.ExportType.WORLD_FULL;
import static ch.sbb.exportservice.model.ExportType.WORLD_FUTURE_TIMETABLE;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportType;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlQueryUtil {

  public static String getFromStatementQueryForWorldOnlyTypes(ExportType exportType, String fromStatement) {
    LocalDate exportDate = LocalDate.now();
    if (exportType.equals(WORLD_FUTURE_TIMETABLE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(fromStatement, DateHelper.getDateAsSqlString(exportDate), DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClauseForWorldOnlyTypes(ExportType exportType, String whereStatement) {
    if (exportType.equals(WORLD_FULL)) {
      return "";
    }
    LocalDate exportDate = LocalDate.now();
    if (exportType.equals(WORLD_FUTURE_TIMETABLE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(whereStatement, DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClause(ExportType exportType, String whereStatement) {
    if (exportType.equals(FULL)) {
      return "";
    }
    if (exportType.equals(ACTUAL)) {
      return String.format(whereStatement, DateHelper.getDateAsSqlString(LocalDate.now()));
    }
    if (exportType.equals(FUTURE_TIMETABLE)) {
      LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
      return String.format(whereStatement, DateHelper.getDateAsSqlString(futureTimeTableYearDate));
    }
    throw new IllegalArgumentException("Value not allowed: " + exportType);
  }

  public static String buildSqlQuery(String... parts) {
    return String.join(" ", parts) + ";";
  }

}
