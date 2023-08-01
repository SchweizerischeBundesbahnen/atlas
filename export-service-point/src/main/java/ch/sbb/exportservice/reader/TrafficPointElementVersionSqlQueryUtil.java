package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.exportservice.model.ExportType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
@Slf4j
public class TrafficPointElementVersionSqlQueryUtil {

  private static final String SELECT_AND_JOIN_STATEMENT = """
      SELECT tpev.*, sbov.*, spv.*, tpevg.*, spv.sloid as parent_service_point_sloid
      FROM traffic_point_element_version as tpev
         LEFT JOIN traffic_point_element_version_geolocation tpevg ON tpevg.id = tpev.traffic_point_geolocation_id
         LEFT JOIN service_point_version spv ON spv.number = tpev.service_point_number
              AND (CASE WHEN current_date between spv.valid_from and spv.valid_to THEN 0 ELSE 1 END = 0)
         LEFT JOIN shared_business_organisation_version sbov ON spv.business_organisation = sbov.sboid
              AND (CASE WHEN current_date between sbov.valid_from and sbov.valid_to THEN 0 ELSE 1 END = 0)
      """;
  private static final String GROUP_BY_STATEMENT = "group by spv.id, tpev.id, sbov.id, tpevg.id";

  private static final String WORLD_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT = "WHERE '%s' between tpev.valid_from and tpev.valid_to ";
  private static final String WORLD_ONLY_ACTUAL_WHERE_STATEMENT = " WHERE '%s' between tpev.valid_from and tpev.valid_to ";

  public String getSqlQuery(ExportType exportType) {
    log.info("ExportType: {}", exportType);
    StringBuilder sqlQueryBuilder = new StringBuilder();
    sqlQueryBuilder.append(SELECT_AND_JOIN_STATEMENT);
    if (getSqlWhereClause(exportType) != null) {
      sqlQueryBuilder.append(getSqlWhereClause(exportType));
    }
    sqlQueryBuilder.append(GROUP_BY_STATEMENT);
    String sqlQuery = sqlQueryBuilder.toString();
    log.info("Execution SQL query: {}\n", sqlQuery);
    return sqlQuery;
  }

  private String getSqlWhereClause(ExportType exportType) {
    LocalDate nextTimetableYearStartDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    if(exportType.equals(ExportType.WORLD_ONLY_ACTUAL)){
      return String.format(WORLD_ONLY_ACTUAL_WHERE_STATEMENT, getDateAsSqlString(LocalDate.now()));
    }
    if(exportType.equals(ExportType.WORLD_ONLY_TIMETABLE_FUTURE)){
      return String.format(WORLD_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT, getDateAsSqlString(nextTimetableYearStartDate));
    }
    if(exportType.equals(ExportType.WORLD_FULL)){
      return "";
    }
    throw  new IllegalStateException("ExportType " + exportType + " not allowed!");
  }

  private String getDateAsSqlString(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }

}
