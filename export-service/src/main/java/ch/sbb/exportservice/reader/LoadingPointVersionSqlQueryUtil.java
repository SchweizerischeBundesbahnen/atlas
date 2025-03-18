package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LoadingPointVersionSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT lpv.*, sbov.*, spv.business_organisation, spv.sloid as parent_service_point_sloid
      FROM loading_point_version as lpv
        LEFT JOIN service_point_version spv ON spv.number = lpv.service_point_number
          AND (CASE WHEN '%s' between spv.valid_from and spv.valid_to THEN 0 ELSE 1 END = 0)
        LEFT JOIN shared_business_organisation_version sbov ON spv.business_organisation = sbov.sboid
          AND (CASE WHEN '%s' between sbov.valid_from and sbov.valid_to THEN 0 ELSE 1 END = 0)
      """;
  private static final String WHERE_STATEMENT = "WHERE '%s' between lpv.valid_from and lpv.valid_to ";
  private static final String GROUP_BY_STATEMENT = "GROUP BY lpv.id, spv.id, sbov.id ";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    log.info("ExportTypeV2: {}", exportTypeV2);
    final String sqlQuery = getFromStatementQueryForWorldOnlyTypes(exportTypeV2, SELECT_STATEMENT)
        + getWhereClauseForWorldOnlyTypes(exportTypeV2, WHERE_STATEMENT)
        + GROUP_BY_STATEMENT;
    log.info("Execution SQL query: {}\n", sqlQuery);
    return sqlQuery;
  }

}
