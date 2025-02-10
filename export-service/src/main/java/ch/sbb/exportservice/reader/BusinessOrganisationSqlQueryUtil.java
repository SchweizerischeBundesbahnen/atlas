package ch.sbb.exportservice.reader;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class BusinessOrganisationSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT DISTINCT bov.*, tc.number, tc.abbreviation, tc.business_register_name, tc.id as transport_company_id
        FROM business_organisation_version as bov
            left join transport_company_relation tcr on bov.sboid = tcr.sboid and
              ('%s' between tcr.valid_from and tcr.valid_to)
            left join transport_company tc on tcr.transport_company_id = tc.id
      """;

  private static final String WHERE_CLAUSE = "WHERE '%s' between bov.valid_from and bov.valid_to";

  private static final String ORDER_BY = "ORDER BY bov.sboid, bov.valid_from ASC";

  public String getSqlQuery(ExportType exportType) {
    final LocalDate date =
        exportType == ExportType.FUTURE_TIMETABLE ? FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now())
            : LocalDate.now();
    final String dateAsSqlString = DateHelper.getDateAsSqlString(date);

    final String sqlQuery = buildSqlQuery(
        SELECT_STATEMENT.formatted(dateAsSqlString),
        exportType == ExportType.FULL ? "" : WHERE_CLAUSE.formatted(dateAsSqlString),
        ORDER_BY
    );

    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
