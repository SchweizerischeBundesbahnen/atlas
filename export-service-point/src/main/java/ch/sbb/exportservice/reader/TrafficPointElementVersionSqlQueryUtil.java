package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.model.ExportType;
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
  private static final String WHERE_STATEMENT = "WHERE '%s' between tpev.valid_from and tpev.valid_to ";
  private static final String GROUP_BY_STATEMENT = "GROUP BY spv.id, tpev.id, sbov.id, tpevg.id ";

  public String getSqlQuery(ExportType exportType) {
    log.info("ExportType: {}", exportType);
    final String sqlQuery = getFromStatementQueryForWorldOnlyTypes(exportType, SELECT_AND_JOIN_STATEMENT)
        + getWhereClauseForWorldOnlyTypes(exportType, WHERE_STATEMENT)
        + GROUP_BY_STATEMENT;
    log.info("Execution SQL query: {}\n", sqlQuery);
    return sqlQuery;
  }

}
