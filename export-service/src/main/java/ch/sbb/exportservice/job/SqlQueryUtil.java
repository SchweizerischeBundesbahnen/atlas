package ch.sbb.exportservice.job;

import static ch.sbb.exportservice.model.ExportTypeV2.ACTUAL;
import static ch.sbb.exportservice.model.ExportTypeV2.FULL;
import static ch.sbb.exportservice.model.ExportTypeV2.FUTURE_TIMETABLE;
import static ch.sbb.exportservice.model.ExportTypeV2.WORLD_FULL;
import static ch.sbb.exportservice.model.ExportTypeV2.WORLD_FUTURE_TIMETABLE;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlQueryUtil {

  public static String getFromStatementQueryForWorldOnlyTypes(ExportTypeV2 exportTypeV2, String fromStatement) {
    LocalDate exportDate = LocalDate.now();
    if (exportTypeV2.equals(WORLD_FUTURE_TIMETABLE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(fromStatement, DateHelper.getDateAsSqlString(exportDate), DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClauseForWorldOnlyTypes(ExportTypeV2 exportTypeV2, String whereStatement) {
    if (exportTypeV2.equals(WORLD_FULL)) {
      return "";
    }
    LocalDate exportDate = LocalDate.now();
    if (exportTypeV2.equals(WORLD_FUTURE_TIMETABLE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(whereStatement, DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClause(ExportTypeV2 exportTypeV2, String whereStatement) {
    if (exportTypeV2.equals(FULL)) {
      return "";
    }
    if (exportTypeV2.equals(ACTUAL)) {
      return String.format(whereStatement, DateHelper.getDateAsSqlString(LocalDate.now()));
    }
    if (exportTypeV2.equals(FUTURE_TIMETABLE)) {
      LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
      return String.format(whereStatement, DateHelper.getDateAsSqlString(futureTimeTableYearDate));
    }
    throw new IllegalArgumentException("Value not allowed: " + exportTypeV2);
  }

  public static String buildSqlQuery(String... parts) {
    return String.join(" ", parts) + ";";
  }

}
