package ch.sbb.exportservice.reader;

import static ch.sbb.exportservice.model.ExportType.ACTUAL;
import static ch.sbb.exportservice.model.ExportType.FULL;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportType;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LineSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = "SELECT lv.* FROM line_version as lv where lv.swissLineNumber is not null";
  private static final String ORDER_BY_STATEMENT = "ORDER BY lv.slnid, lv.validFrom ASC";

  public String getSqlQuery(ExportType exportType) {
    String additionalWhereClause = "";
    if (exportType != FULL) {
      String date = DateHelper.getDateAsSqlString(exportType == ACTUAL ? LocalDate.now()
          : FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "AND '%s' >= lv.validFrom AND '%s' <= lv.validTo".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
