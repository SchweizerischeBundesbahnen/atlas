package ch.sbb.exportservice.job.sepodi.servicepoint.sql;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ServicePointVersionSqlQueryUtil {

  private static final String SELECT_AND_JOIN_STATEMENT = """
      SELECT spv.id, string_agg(spvmot.means_of_transport, '|') as list_of_transports, string_agg(spvc.categories, '|') as list_of_categories,
      spv.*, spvg.country as geolocation_country, spvg.*, sbov.*, spfc.*
      FROM service_point_version spv
      LEFT JOIN service_point_version_means_of_transport spvmot on spv.id = spvmot.service_point_version_id
      LEFT JOIN service_point_version_categories spvc on spv.id = spvc.service_point_version_id
      LEFT JOIN service_point_version_geolocation spvg on spv.service_point_geolocation_id = spvg.id
      LEFT JOIN service_point_fot_comment spfc on spv.number = spfc.service_point_number
      LEFT JOIN shared_business_organisation_version sbov on spv.business_organisation = sbov.sboid
            AND (CASE WHEN '%s' between sbov.valid_from and sbov.valid_to THEN 0 ELSE 1 END = 0)
      """;
  private static final String GROUP_BY_STATEMENT = "group by spv.id, spvg.id, sbov.id, spfc.service_point_number";

  private static final String SWISS_ONLY_FULL_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') ";
  private static final String SWISS_ONLY_ACTUAL_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') "
      + "AND '%s' between spv.valid_from and spv.valid_to ";
  private static final String SWISS_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') "
      + "AND '%s' between spv.valid_from and spv.valid_to ";

  private static final String WORLD_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT = "WHERE "
      + "'%s' between spv.valid_from and spv.valid_to ";
  private static final String WORLD_ONLY_ACTUAL_WHERE_STATEMENT = " WHERE '%s' between spv.valid_from and spv.valid_to ";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    log.info("ExportTypeV2: {}", exportTypeV2);
    StringBuilder sqlQueryBuilder = new StringBuilder(getFromStatementQuery(exportTypeV2));
    if (getSqlWhereClause(exportTypeV2) != null) {
      sqlQueryBuilder.append(getSqlWhereClause(exportTypeV2));
    }
    sqlQueryBuilder.append(GROUP_BY_STATEMENT);
    String sqlQuery = sqlQueryBuilder.toString();
    log.info("Execution SQL query: {}\n", sqlQuery);
    return sqlQuery;
  }

  private String getSqlWhereClause(ExportTypeV2 exportTypeV2) {
    LocalDate nextTimetableYearStartDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    return switch (exportTypeV2) {
      case SWISS_FULL -> SWISS_ONLY_FULL_WHERE_STATEMENT;
      case SWISS_ACTUAL -> String.format(SWISS_ONLY_ACTUAL_WHERE_STATEMENT, DateHelper.getDateAsSqlString(LocalDate.now()));
      case SWISS_FUTURE_TIMETABLE ->
          String.format(SWISS_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT, DateHelper.getDateAsSqlString(nextTimetableYearStartDate));
      case WORLD_ACTUAL -> String.format(WORLD_ONLY_ACTUAL_WHERE_STATEMENT, DateHelper.getDateAsSqlString(LocalDate.now()));
      case WORLD_FUTURE_TIMETABLE ->
          String.format(WORLD_ONLY_FUTURE_TIMETABLE_WHERE_STATEMENT, DateHelper.getDateAsSqlString(nextTimetableYearStartDate));
      case WORLD_FULL -> "";
      default -> throw new IllegalStateException(exportTypeV2.name() + " is not allowed here.");
    };
  }

  private String getFromStatementQuery(ExportTypeV2 exportTypeV2) {
    LocalDate nextTimetableYearStartDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    return switch (exportTypeV2) {
      case SWISS_FULL, SWISS_ACTUAL,
           WORLD_FULL, WORLD_ACTUAL -> String.format(SELECT_AND_JOIN_STATEMENT, DateHelper.getDateAsSqlString(LocalDate.now()));
      case WORLD_FUTURE_TIMETABLE, SWISS_FUTURE_TIMETABLE ->
          String.format(SELECT_AND_JOIN_STATEMENT, DateHelper.getDateAsSqlString(nextTimetableYearStartDate));
      default -> throw new IllegalStateException(exportTypeV2.name() + " is not allowed here.");
    };
  }

}
