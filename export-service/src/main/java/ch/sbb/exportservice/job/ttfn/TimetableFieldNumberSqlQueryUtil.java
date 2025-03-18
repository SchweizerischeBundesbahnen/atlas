package ch.sbb.exportservice.job.ttfn;

import static ch.sbb.exportservice.model.ExportTypeV2.ACTUAL;
import static ch.sbb.exportservice.model.ExportTypeV2.FULL;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
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
  private static final String ORDER_BY_STATEMENT = "ORDER BY tv.ttfnid, tv.valid_from";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String additionalWhereClause = "";
    if (exportTypeV2 != FULL) {
      String date = DateHelper.getDateAsSqlString(exportTypeV2 == ACTUAL ? LocalDate.now()
          : FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "WHERE '%s' >= tv.valid_from AND '%s' <= tv.valid_to".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, GROUP_BY_STATEMENT, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
