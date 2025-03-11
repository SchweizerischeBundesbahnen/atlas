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
public class SublineSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT sv.*
      FROM subline_version as sv
      """;
  private static final String ORDER_BY_STATEMENT = "ORDER BY sv.slnid, sv.valid_from";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    final String today = DateHelper.getDateAsSqlString(LocalDate.now());
    String additionalWhereClause = "";
    if (exportTypeV2 != FULL) {
      final String date = exportTypeV2 == ACTUAL ? today :
          DateHelper.getDateAsSqlString(FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "WHERE '%s' >= sv.valid_from AND '%s' <= sv.valid_to".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
