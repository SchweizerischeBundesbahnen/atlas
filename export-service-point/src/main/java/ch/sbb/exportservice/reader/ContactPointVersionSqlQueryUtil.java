package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.model.PrmExportType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ContactPointVersionSqlQueryUtil extends SqlQueryUtil {

    private static final String SELECT_STATEMENT = """
      SELECT cpv.*
      FROM contact_point_version cpv
      """;
    private static final String WHERE_STATEMENT = "WHERE '%s' between cpv.valid_from and cpv.valid_to";
    private static final String GROUP_BY_STATEMENT = "GROUP BY cpv.id";

    public String getSqlQuery(PrmExportType exportType) {
        final String sqlQuery =
                SELECT_STATEMENT
                        + getWhereClause(exportType, WHERE_STATEMENT)
                        + GROUP_BY_STATEMENT;
        log.info("Execution SQL query:");
        log.info(sqlQuery);
        return sqlQuery;
    }

}
