package ch.sbb.exportservice.job.sepodi.trafficpoint.sql;

import static ch.sbb.exportservice.model.ExportTypeV2.WORLD_FUTURE_TIMETABLE;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class TrafficPointElementVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_AND_JOIN_STATEMENT = """
      SELECT tpev.*, sbov.*, spv.*, tpevg.*, spv.sloid as parent_service_point_sloid
      FROM traffic_point_element_version as tpev
         LEFT JOIN traffic_point_element_version_geolocation tpevg ON tpevg.id = tpev.traffic_point_geolocation_id
         LEFT JOIN service_point_version spv ON spv.number = tpev.service_point_number
              AND (CASE WHEN '%s' between spv.valid_from and spv.valid_to THEN 0 ELSE 1 END = 0)
         LEFT JOIN shared_business_organisation_version sbov ON spv.business_organisation = sbov.sboid
              AND (CASE WHEN '%s' between sbov.valid_from and sbov.valid_to THEN 0 ELSE 1 END = 0)
      """;

  private static final String GROUP_BY_STATEMENT = "GROUP BY spv.id, tpev.id, sbov.id, tpevg.id ";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    LocalDate date =
        exportTypeV2 == WORLD_FUTURE_TIMETABLE ? FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now())
            : LocalDate.now();

    String dateAsSqlString = DateHelper.getDateAsSqlString(date);

    log.info("ExportTypeV2: {}", exportTypeV2);

    String query = ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .validFromIdentifier("tpev.valid_from")
        .validToIdentifier("tpev.valid_to")
        .selectStatement(SELECT_AND_JOIN_STATEMENT.formatted(dateAsSqlString, dateAsSqlString))
        .groupByAndOrderByClause(GROUP_BY_STATEMENT)
        .build()
        .getQuery();

    log.info("Execution SQL query: {}\n", query);
    return query;
  }

}
