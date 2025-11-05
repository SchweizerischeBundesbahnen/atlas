package ch.sbb.exportservice.job.sepodi.servicepoint.sql;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ServicePointVersionSqlQueryUtil extends SqlQueryUtil {

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

  private static final String SWISS_WHERE_STATEMENT = "WHERE spv.country "
      + "IN('SWITZERLAND','GERMANY_BUS','AUSTRIA_BUS','ITALY_BUS','FRANCE_BUS') ";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    log.info("ExportTypeV2: {}", exportTypeV2);

    String query = ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .validFromIdentifier("spv.valid_from")
        .validToIdentifier("spv.valid_to")
        .selectStatement(getFromStatementQuery(exportTypeV2))
        .whereClause(getSqlWhereClause(exportTypeV2))
        .groupByAndOrderByClause(GROUP_BY_STATEMENT)
        .build()
        .getQuery();

    log.info("Execution SQL query: {}\n", query);
    return query;
  }

  private String getSqlWhereClause(ExportTypeV2 exportTypeV2) {
    return switch (exportTypeV2) {
      case SWISS_FULL, SWISS_ACTUAL, SWISS_FUTURE_TIMETABLE, SWISS_TIMETABLE_YEARS -> SWISS_WHERE_STATEMENT;
      case WORLD_ACTUAL, WORLD_FUTURE_TIMETABLE, WORLD_FULL, WORLD_TIMETABLE_YEARS -> null;
      default -> throw new IllegalStateException(exportTypeV2.name() + " is not allowed here.");
    };
  }

  private String getFromStatementQuery(ExportTypeV2 exportTypeV2) {
    LocalDate nextTimetableYearStartDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    return switch (exportTypeV2) {
      case SWISS_FULL, SWISS_ACTUAL, SWISS_TIMETABLE_YEARS,
           WORLD_FULL, WORLD_ACTUAL, WORLD_TIMETABLE_YEARS -> String.format(SELECT_AND_JOIN_STATEMENT,
          DateHelper.getDateAsSqlString(LocalDate.now()));
      case WORLD_FUTURE_TIMETABLE, SWISS_FUTURE_TIMETABLE ->
          String.format(SELECT_AND_JOIN_STATEMENT, DateHelper.getDateAsSqlString(nextTimetableYearStartDate));
      default -> throw new IllegalStateException(exportTypeV2.name() + " is not allowed here.");
    };
  }

}
