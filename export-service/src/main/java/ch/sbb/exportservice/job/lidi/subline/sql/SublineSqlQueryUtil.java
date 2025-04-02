package ch.sbb.exportservice.job.lidi.subline.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
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
    String sqlQuery = ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .selectStatement(SELECT_STATEMENT)
        .groupByAndOrderByClause(ORDER_BY_STATEMENT)
        .build()
        .getQuery();
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
