package ch.sbb.exportservice.reader;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TimetableFieldNumberSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = "SELECT tv FROM timetable_field_number_version as tv";
  private static final String ORDER_BY_STATEMENT = "ORDER BY tv.ttfnid, tv.validFrom ASC";

  public String getSqlQuery(ExportType exportType) {
    String additionalWhereClause = "";
    if (exportType != ExportType.FULL) {
      String date = DateHelper.getDateAsSqlString(exportType == ExportType.ACTUAL_DATE ? LocalDate.now()
          : FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "WHERE '%s' >= tv.validFrom AND '%s' <= tv.validTo".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
