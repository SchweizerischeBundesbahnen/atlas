package ch.sbb.exportservice.job.sepodi.sectorgroup.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SectorGroupSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = "SELECT sec.* FROM sector_group_version sec";
  private static final String ORDER_BY_STATEMENT = "order by sec.sloid, sec.valid_from ASC";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .selectStatement(SELECT_STATEMENT)
        .groupByAndOrderByClause(ORDER_BY_STATEMENT)
        .build().getQuery();
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
