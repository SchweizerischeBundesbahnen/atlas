package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.model.PrmExportType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ReferencePointVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT rpv.*
      FROM reference_point_version rpv
      """;
  private static final String WHERE_STATEMENT = "WHERE '%s' between rpv.valid_from and rpv.valid_to";
  private static final String GROUP_BY_STATEMENT = "GROUP BY rpv.id";

  public String getSqlQuery(PrmExportType exportType) {
    final String sqlQuery = getWholeSqlStatement(exportType, SELECT_STATEMENT, WHERE_STATEMENT, GROUP_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
