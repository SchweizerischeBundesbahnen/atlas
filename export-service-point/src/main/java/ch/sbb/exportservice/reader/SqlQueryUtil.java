package ch.sbb.exportservice.reader;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiExportType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@Slf4j
public class SqlQueryUtil {

  public static String getFromStatementQueryForWorldOnlyTypes(ExportTypeBase exportType, String fromStatement) {
    LocalDate exportDate = LocalDate.now();
    if (exportType.equals(SePoDiExportType.WORLD_ONLY_TIMETABLE_FUTURE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(fromStatement, DateHelper.getDateAsSqlString(exportDate), DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWhereClauseForWorldOnlyTypes(ExportTypeBase exportType, String whereStatement) {
    if (exportType.equals(SePoDiExportType.WORLD_FULL)) {
      return "";
    }
    LocalDate exportDate = LocalDate.now();
    if (exportType.equals(SePoDiExportType.WORLD_ONLY_TIMETABLE_FUTURE)) {
      exportDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    }
    return String.format(whereStatement, DateHelper.getDateAsSqlString(exportDate));
  }

  public static String getWholeSqlStatement(PrmExportType exportType, String selectStatement, String whereStatement, String groupByStatement) {
    String spaceInSqlStatement = StringUtils.SPACE;
    String semicolonAtTheEndOfSqlStatement = ";";
    if (exportType.equals(PrmExportType.FULL)) {
      return selectStatement + spaceInSqlStatement + groupByStatement + semicolonAtTheEndOfSqlStatement;
    }
    if(exportType.equals(PrmExportType.ACTUAL)){
      return String.format(selectStatement + spaceInSqlStatement + whereStatement + spaceInSqlStatement + groupByStatement + semicolonAtTheEndOfSqlStatement,
              DateHelper.getDateAsSqlString(LocalDate.now()));
    }
    if (exportType.equals(PrmExportType.TIMETABLE_FUTURE)) {
      LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
      return String.format(selectStatement + spaceInSqlStatement + whereStatement + spaceInSqlStatement + groupByStatement + semicolonAtTheEndOfSqlStatement,
              DateHelper.getDateAsSqlString(futureTimeTableYearDate));
    }
    throw new IllegalArgumentException("Value not allowed: " + exportType);
  }

}
