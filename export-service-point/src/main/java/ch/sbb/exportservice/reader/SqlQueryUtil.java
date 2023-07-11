package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.exportservice.model.ServicePointExportType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SqlQueryUtil {

  public static final String FUTURE_TIMETABLE_DATE = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()).format(
          DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));

  public static final String NOW_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));

  private static final String SELECT_AND_JOIN_STATEMENT =
      "SELECT spv.id, string_agg(spvmot.means_of_transport, '|') as list_of_transports, string_agg(spvc.categories, '|') "
          + "as list_of_categories, spv.*, spvg.country as geolocation_country, spvg.*, sbov.* "
          + "FROM service_point_version spv "
          + "LEFT JOIN service_point_version_means_of_transport spvmot "
          + "on spv.id = spvmot.service_point_version_id "
          + "LEFT JOIN service_point_version_categories spvc on spv.id = spvc.service_point_version_id "
          + "LEFT JOIN service_point_version_geolocation spvg on spv.service_point_geolocation_id = spvg.id "
          + "LEFT JOIN shared_business_organisation_version sbov on spv.business_organisation = sbov.sboid " +
              "AND (CASE WHEN current_date between sbov.valid_from and sbov.valid_to THEN 0 " +
              "ELSE 1 " +
              "END = 0)";
  private static final String GROUP_BY_STATEMENT = "group by spv.id, spvg.id, sbov.id";
  private static final String SWISS_ONLY_FULL_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') ";
  private static final String SWISS_ONLY_ACTUAL_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') "
      + "AND '"+ NOW_DATE +"' between spv.valid_from and spv.valid_to ";

  private static final String SWISS_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') "
      + "AND '" + FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()).format(
      DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN)) + "' between spv.valid_from and spv.valid_to ";

  private static final String WORLD_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT = "WHERE "
      + "'" + FUTURE_TIMETABLE_DATE + "' between spv.valid_from and spv.valid_to ";
  private static final String WORLD_ONLY_ACTUAL_WHERE_STATEMENT = " WHERE '"+NOW_DATE +"' between spv.valid_from and spv.valid_to ";

  public String getSqlQuery(ServicePointExportType exportType) {
    log.info("ExportType: {}", exportType);
    StringBuilder sqlQueryBuilder = new StringBuilder();
    sqlQueryBuilder.append(SELECT_AND_JOIN_STATEMENT);
    if (getSqlWhereClause(exportType) != null) {
      sqlQueryBuilder.append(getSqlWhereClause(exportType));
    }
    sqlQueryBuilder.append(GROUP_BY_STATEMENT);
    String sqlQuery = sqlQueryBuilder.toString();
    log.info("Execution SQL query: {}", sqlQuery);
    return sqlQuery;
  }

  private String getSqlWhereClause(ServicePointExportType exportType) {
    return switch (exportType) {
      case SWISS_ONLY_FULL -> SWISS_ONLY_FULL_WHERE_STATEMENT;
      case SWISS_ONLY_ACTUAL -> SWISS_ONLY_ACTUAL_WHERE_STATEMENT;
      case SWISS_ONLY_TIMETABLE_FUTURE -> SWISS_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT;
      case WORLD_ONLY_ACTUAL -> WORLD_ONLY_ACTUAL_WHERE_STATEMENT;
      case WORLD_ONLY_TIMETABLE_FUTURE -> WORLD_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT;
      case WORLD_FULL -> "";
    };
  }

}
