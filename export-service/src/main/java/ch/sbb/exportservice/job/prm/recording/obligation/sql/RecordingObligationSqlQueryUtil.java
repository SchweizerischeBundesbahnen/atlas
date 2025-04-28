package ch.sbb.exportservice.job.prm.recording.obligation.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RecordingObligationSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT *
      FROM recording_obligation where recording_obligation=false
      """;

  public String getSqlQuery() {
    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
