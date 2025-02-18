package ch.sbb.exportservice.reader;

import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TimetableFieldNumberSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT tv.*, string_agg(tflr.slnid, '|') as slnids
          FROM timetable_field_number_version as tv
          left join timetable_field_line_relation tflr on tv.id = tflr.timetable_field_version_id
      """;
  private static final String GROUP_BY_STATEMENT = "group by tv.id, tv.ttfnid, tv.valid_from";
  private static final String ORDER_BY_STATEMENT = "ORDER BY tv.ttfnid, tv.validFrom";

  public String getSqlQuery(ExportType exportType) {
    String additionalWhereClause = "";
    if (exportType != ExportType.FULL) {
      String date = DateHelper.getDateAsSqlString(exportType == ExportType.ACTUAL_DATE ? LocalDate.now()
          : FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "WHERE '%s' >= tv.validFrom AND '%s' <= tv.validTo".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, GROUP_BY_STATEMENT, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
