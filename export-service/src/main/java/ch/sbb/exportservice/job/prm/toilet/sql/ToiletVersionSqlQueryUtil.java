package ch.sbb.exportservice.job.prm.toilet.sql;

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
  private static final String GROUP_BY_STATEMENT = "GROUP BY toi.id";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = getBaseSqlQuery(SELECT_STATEMENT, GROUP_BY_STATEMENT, exportTypeV2);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
