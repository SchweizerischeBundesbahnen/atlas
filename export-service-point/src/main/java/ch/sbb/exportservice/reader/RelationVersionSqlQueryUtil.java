package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.model.PrmExportType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RelationVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT rv.*
      FROM relation_version rv
      """;
  private static final String WHERE_STATEMENT = "WHERE '%s' between rv.valid_from and rv.valid_to";
  private static final String GROUP_BY_STATEMENT = "GROUP BY rv.id";

  public String getSqlQuery(PrmExportType exportType) {
    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, getWhereClause(exportType, WHERE_STATEMENT), GROUP_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
