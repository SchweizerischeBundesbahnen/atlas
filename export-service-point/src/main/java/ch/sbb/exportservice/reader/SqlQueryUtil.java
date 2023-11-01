package ch.sbb.exportservice.reader;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.time.LocalDate;

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

}
