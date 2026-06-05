package ch.sbb.exportservice.job.prm.relation.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RelationVersionSqlQueryUtil extends SqlQueryUtil {

  public static final String SELECT_STATEMENT = """
      SELECT rv.*
      FROM relation_version rv
      """;
  public static final String GROUP_BY_STATEMENT = "GROUP BY rv.id";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = buildSqlQuery(SELECT_STATEMENT, GROUP_BY_STATEMENT, exportTypeV2);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
