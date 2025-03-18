package ch.sbb.exportservice.job.transportcompany;

import ch.sbb.exportservice.job.SqlQueryUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TransportCompanySqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT tc.*
      FROM transport_company tc
      """;

  public String getSqlQuery() {
    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }
}
