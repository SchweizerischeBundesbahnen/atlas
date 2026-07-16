package ch.sbb.exportservice.job;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.helper.DateHelper;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.exportservice.job.SqlQueryUtil.ExportSqlQueryBuilder;
import ch.sbb.exportservice.job.SqlQueryUtil.ExportSqlQueryBuilder.ExportSqlQueryBuilderBuilder;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SqlQueryUtilTest {

  public static final ExportSqlQueryBuilderBuilder EXPORT_BUSINESS_ORGANISATION_SQL_QUERY_BUILDER =
      ExportSqlQueryBuilder.builder()
          .selectStatement("select * from business_organisation bov")
          .groupByAndOrderByClause("group by bov.id");

  @Test
  void shouldReturnSqlStatementsForFullBusinessOrganisation() {
    String sqlQuery = EXPORT_BUSINESS_ORGANISATION_SQL_QUERY_BUILDER
        .exportType(ExportTypeV2.FULL)
        .build()
        .getQuery();

    assertThat(sqlQuery).isEqualTo("select * from business_organisation bov WHERE 1=1 group by bov.id;");
  }

  @Test
  void shouldReturnSqlStatementsForActualBusinessOrganisation() {
    String sqlQuery = EXPORT_BUSINESS_ORGANISATION_SQL_QUERY_BUILDER
        .exportType(ExportTypeV2.ACTUAL)
        .validFromIdentifier("bov.valid_from")
        .validToIdentifier("bov.valid_to")
        .build()
        .getQuery();

    String today = DateHelper.getDateAsSqlString(LocalDate.now());
    assertThat(sqlQuery).isEqualTo("""
        select * from business_organisation bov WHERE '%s' >= bov.valid_from AND '%s' <= bov.valid_to group by bov.id;""".formatted(
        today, today));
  }

  @Test
  void shouldReturnSqlStatementsForTimetableYearsBusinessOrganisation() {
    String sqlQuery = EXPORT_BUSINESS_ORGANISATION_SQL_QUERY_BUILDER
        .exportType(ExportTypeV2.TIMETABLE_YEARS)
        .build()
        .getQuery();

    DateRange timetableYearsDateRange = ExportYearsTimetableUtil.getTimetableYearsDateRange();
    String timetableYearsStart = DateHelper.getDateAsSqlString(timetableYearsDateRange.getFrom());
    String timetableYearsEnd = DateHelper.getDateAsSqlString(timetableYearsDateRange.getTo());
    assertThat(sqlQuery).isEqualTo("""
        select * from business_organisation bov WHERE '%s' <= valid_to  AND valid_from <= '%s' group by bov.id;""".formatted(
        timetableYearsStart, timetableYearsEnd));
  }

}
