package ch.sbb.exportservice.job.lidi.ttfn.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TimetableFieldNumberSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT tv.*
          FROM timetable_field_number_version as tv
      """;
  private static final String GROUP_BY_ORDER_BY_CLAUSE = """
      GROUP BY tv.id, tv.ttfnid, tv.valid_from ORDER BY tv.ttfnid, tv.valid_from
      """;

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .validFromIdentifier("tv.valid_from")
        .validToIdentifier("tv.valid_to")
        .selectStatement(SELECT_STATEMENT)
        .groupByAndOrderByClause(GROUP_BY_ORDER_BY_CLAUSE)
        .build()
        .getQuery();

    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
