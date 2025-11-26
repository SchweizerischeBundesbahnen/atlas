package ch.sbb.exportservice.job.lidi.line.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LineSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = "SELECT lv.* FROM line_version as lv";
  private static final String ORDER_BY_STATEMENT = "ORDER BY lv.slnid, lv.valid_from ASC";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = getBaseSqlQuery(SELECT_STATEMENT, ORDER_BY_STATEMENT, exportTypeV2);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
