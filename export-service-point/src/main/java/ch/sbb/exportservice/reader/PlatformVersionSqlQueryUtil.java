package ch.sbb.exportservice.reader;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.PrmExportType;
import java.time.LocalDate;
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
    private static final String WHERE_STATEMENT = "WHERE '%s' between pv.valid_from and pv.valid_to ";
    private static final String GROUP_BY_STATEMENT = "GROUP BY pv.id";


    public String getSqlQuery(PrmExportType exportType) {
        final String sqlQuery =
            SELECT_STATEMENT
                + getWhereClause(exportType, WHERE_STATEMENT)
                + GROUP_BY_STATEMENT;
        log.info("Execution SQL query:");
        log.info(sqlQuery);
        return sqlQuery;
    }

    public String getWhereClause(PrmExportType exportType, String whereStatement) {
        if (exportType.equals(PrmExportType.FULL)) {
            return "";
        }
        if(exportType.equals(PrmExportType.ACTUAL)){
            return String.format(whereStatement, DateHelper.getDateAsSqlString(LocalDate.now()));
        }
        if (exportType.equals(PrmExportType.TIMETABLE_FUTURE)) {
            LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
            return String.format(whereStatement, DateHelper.getDateAsSqlString(futureTimeTableYearDate));
        }
        throw new IllegalArgumentException("Value not allowed: " + exportType);
    }
}
