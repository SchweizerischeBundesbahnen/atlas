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

  private static final String SELECT_AND_JOIN_STATEMENT =
      "SELECT spv.id, string_agg(spvmot.means_of_transport, '|') as list_of_transports, string_agg(spvc.categories, '|') "
          + "as list_of_categories, spv.*, spvg.* "
          + "FROM service_point_version spv "
          + "LEFT JOIN service_point_version_means_of_transport spvmot "
          + "on spv.id = spvmot.service_point_version_id "
          + "LEFT JOIN service_point_version_categories spvc on spv.id = spvc.service_point_version_id "
          + "LEFT JOIN service_point_version_geolocation spvg on spv.service_point_geolocation_id = spvg.id ";
  private static final String GROUP_BY_STATEMENT = "group by spv.id,spvg.id";
  private static final String SWISS_ONLY_FULL_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') ";
  private static final String SWISS_ONLY_ACTUAL_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') "
      + "AND now() between spv.valid_from and spv.valid_to ";

  private static final String SWISS_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') "
      + "AND spv.valid_from >= '" + FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now()).format(
      DateTimeFormatter.ofPattern(
          AtlasApiConstants.DATE_FORMAT_PATTERN)) + "' ";
  private static final String WORLD_ONLY_ACTUAL_WHERE_STATEMENT = "WHERE spv.country "
      + "AND now() between spv.valid_from and spv.valid_to ";

  public String getSqlQuery(ServicePointExportType exportType) {
    log.warn("exportType: {}", exportType);
    StringBuilder sqlQueryBuilder = new StringBuilder();
    sqlQueryBuilder.append(SELECT_AND_JOIN_STATEMENT);
    if (getSqlWhereClause(exportType) != null) {
      sqlQueryBuilder.append(getSqlWhereClause(exportType));
    }
    sqlQueryBuilder.append(GROUP_BY_STATEMENT);
    return sqlQueryBuilder.toString();
  }

  private String getSqlWhereClause(ServicePointExportType exportType) {
    if (exportType.equals(ServicePointExportType.SWISS_ONLY_FULL)) {
      return SWISS_ONLY_FULL_WHERE_STATEMENT;
    }
    if (exportType.equals(ServicePointExportType.SWISS_ONLY_ACTUAL)) {
      return SWISS_ONLY_ACTUAL_WHERE_STATEMENT;
    }
    if (exportType.equals(ServicePointExportType.SWISS_ONLY_TIMETABLE_FUTURE)) {
      return SWISS_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT;
    }
    if (exportType.equals(ServicePointExportType.WORLD_ONLY_ACTUAL)) {
      return WORLD_ONLY_ACTUAL_WHERE_STATEMENT;
    }

    return null;
  }

}
