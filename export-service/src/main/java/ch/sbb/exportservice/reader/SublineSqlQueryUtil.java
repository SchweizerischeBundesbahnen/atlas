package ch.sbb.exportservice.reader;

import static ch.sbb.exportservice.model.ExportTypeV2.ACTUAL;
import static ch.sbb.exportservice.model.ExportTypeV2.FULL;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SublineSqlQueryUtil extends SqlQueryUtil {

  private static final String LINE_VIEW = """
      select *
      from (
               select f.*, v.valid_from, v.valid_to
               from (
                        select swiss_line_number,
                               number,
                               description,
                               short_number,
                               offer_category,
                               status,
                               line_type,
                               business_organisation,
                               slnid
                        from (
                                 select distinct on (slnid) *
                                 from ((select distinct on (slnid) 1 as rank,
                                                                   swiss_line_number,
                                                                   number,
                                                                   description,
                                                                   short_number,
                                                                   offer_category,
                                                                   status,
                                                                   line_type,
                                                                   business_organisation,
                                                                   slnid,
                                                                   valid_from,
                                                                   valid_to
                                        from line_version
                                        where valid_from <= current_date
                                          and current_date <= valid_to)
                                       union all
                                       (select distinct on (slnid) 2 as rank,
                                                                   swiss_line_number,
                                                                   number,
                                                                   description,
                                                                   short_number,
                                                                   offer_category,
                                                                   status,
                                                                   line_type,
                                                                   business_organisation,
                                                                   slnid,
                                                                   valid_from,
                                                                   valid_to
                                        from line_version
                                        where valid_from >= current_date
                                        order by slnid, valid_from)
                                       union all
                                       (select distinct on (slnid) 3 as rank,
                                                                   swiss_line_number,
                                                                   number,
                                                                   description,
                                                                   short_number,
                                                                   offer_category,
                                                                   status,
                                                                   line_type,
                                                                   business_organisation,
                                                                   slnid,
                                                                   valid_from,
                                                                   valid_to
                                        from line_version
                                        where valid_to <= current_date
                                        order by slnid, valid_to desc)) as ranked order by slnid, rank
                             ) as chosen
                    ) f
                        join (
                   select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to
                   from line_version
                   group by slnid
               ) v on f.slnid = v.slnid
           )
      """;

  private static final String SELECT_STATEMENT = """
      SELECT sv.*, parent.offer_category, parent.short_number, parent.number as line_number, parent.swiss_line_number
      FROM subline_version as sv
      join (""" + LINE_VIEW + """
      ) as parent
      on sv.mainline_slnid = parent.slnid
      """;
  private static final String ORDER_BY_STATEMENT = "ORDER BY sv.slnid, sv.valid_from";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    final String today = DateHelper.getDateAsSqlString(LocalDate.now());
    String additionalWhereClause = "";
    if (exportTypeV2 != FULL) {
      final String date = exportTypeV2 == ACTUAL ? today :
          DateHelper.getDateAsSqlString(FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
      additionalWhereClause = "WHERE '%s' >= sv.valid_from AND '%s' <= sv.valid_to".formatted(date, date);
    }

    final String sqlQuery = buildSqlQuery(SELECT_STATEMENT, additionalWhereClause, ORDER_BY_STATEMENT);
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
