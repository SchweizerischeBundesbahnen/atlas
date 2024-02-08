package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.model.PrmExportType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PlatformVersionSqlQueryUtil extends SqlQueryUtil{

    private static final String SELECT_STATEMENT = """
      SELECT pv.id, string_agg(pvio.info_opportunities, '|') as info_opportunities, pv.* 
      FROM platform_version pv
      LEFT JOIN platform_version_info_opportunities pvio on pv.id = pvio.platform_version_id
      """;
    private static final String WHERE_STATEMENT = "WHERE '%s' between pv.valid_from and pv.valid_to";
    private static final String GROUP_BY_STATEMENT = "GROUP BY pv.id";

    public String getSqlQuery(PrmExportType exportType) {
        final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, getWhereClause(exportType, WHERE_STATEMENT), GROUP_BY_STATEMENT);
        log.info("Execution SQL query:");
        log.info(sqlQuery);
        return sqlQuery;
    }
}
