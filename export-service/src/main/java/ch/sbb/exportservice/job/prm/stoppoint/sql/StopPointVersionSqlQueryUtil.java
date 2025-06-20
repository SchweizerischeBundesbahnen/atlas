package ch.sbb.exportservice.job.prm.stoppoint.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class StopPointVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT spv.id, string_agg(spvmot.means_of_transport, '|') as list_of_transports, spv.*
      FROM stop_point_version spv
        LEFT JOIN stop_point_version_means_of_transport spvmot on spv.id = spvmot.stop_point_version_id
      """;
  private static final String WHERE_STATEMENT = "WHERE '%s' between spv.valid_from and spv.valid_to";
  private static final String GROUP_BY_STATEMENT = "GROUP BY spv.id";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, getWhereClause(exportTypeV2, WHERE_STATEMENT), GROUP_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }
}
