package ch.sbb.exportservice.reader;

import static ch.sbb.exportservice.model.ExportType.ACTUAL;
import static ch.sbb.exportservice.model.ExportType.FULL;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportType;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SublineSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      SELECT sv.*, parent.offer_category, parent.short_number, parent.number as line_number, parent.prio
      FROM subline_version as sv
               left join (select lv.slnid, string_agg(lv.offer_category, '|') as offer_category,
                                 string_agg(lv.short_number, '|') as short_number,
                                 string_agg(lv.number, '|') as number,
                                 string_agg(
                                         case
                                             when '2025-02-18' between lv.valid_from and lv.valid_to then '1'
                                             when '2025-02-18' < lv.valid_from then '2'
                                             else '3' end, '|') as prio
                          from (select * from line_version order by valid_from) lv group by lv.slnid) parent
                         on sv.mainline_slnid = parent.slnid
      """;
  private static final String ORDER_BY_STATEMENT = "ORDER BY sv.slnid, sv.validFrom";

  public String getSqlQuery(ExportType exportType) {
    final String today = DateHelper.getDateAsSqlString(LocalDate.now());
    String additionalWhereClause = "";
    if (exportType != FULL) {
      final String date = exportType == ACTUAL ? today :
          DateHelper.getDateAsSqlString(FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "WHERE '%s' >= sv.validFrom AND '%s' <= sv.validTo".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT.formatted(today, today), additionalWhereClause, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
