package ch.sbb.exportservice.job.prm.platform.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PlatformVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT pv.id, string_agg(pvio.info_opportunities, '|') as info_opportunities, pv.* 
      FROM platform_version pv
      LEFT JOIN platform_version_info_opportunities pvio on pv.id = pvio.platform_version_id
      """;
  private static final String GROUP_BY_STATEMENT = "GROUP BY pv.id";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = buildSqlQuery(SELECT_STATEMENT, GROUP_BY_STATEMENT, exportTypeV2);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }
}
