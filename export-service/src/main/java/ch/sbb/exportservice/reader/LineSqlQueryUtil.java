package ch.sbb.exportservice.reader;

import static ch.sbb.exportservice.model.ExportTypeV2.ACTUAL;
import static ch.sbb.exportservice.model.ExportTypeV2.FULL;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LineSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = "SELECT lv.* FROM line_version as lv where lv.swiss_line_number is not null";
  private static final String ORDER_BY_STATEMENT = "ORDER BY lv.slnid, lv.valid_from ASC";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String additionalWhereClause = "";
    if (exportTypeV2 != FULL) {
      String date = DateHelper.getDateAsSqlString(exportTypeV2 == ACTUAL ? LocalDate.now()
          : FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "AND '%s' >= lv.valid_from AND '%s' <= lv.valid_to".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
