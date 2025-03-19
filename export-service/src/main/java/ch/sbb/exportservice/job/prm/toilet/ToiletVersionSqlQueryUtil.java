package ch.sbb.exportservice.job.prm.toilet;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ToiletVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT toi.*
      FROM toilet_version toi
      """;
  private static final String WHERE_STATEMENT = "WHERE '%s' between toi.valid_from and toi.valid_to";
  private static final String GROUP_BY_STATEMENT = "GROUP BY toi.id";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, getWhereClause(exportTypeV2, WHERE_STATEMENT), GROUP_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
