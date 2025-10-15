package ch.sbb.exportservice.job;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.job.SqlQueryUtil.ExportSqlQueryBuilder;
import ch.sbb.exportservice.job.SqlQueryUtil.ExportSqlQueryBuilder.ExportSqlQueryBuilderBuilder;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class SqlQueryUtilTest {

  public static final ExportSqlQueryBuilderBuilder EXPORT_BUSINESS_ORGANISATION_SQL_QUERY_BUILDER =
      ExportSqlQueryBuilder.builder()
          .selectStatement("select * from business_organisation bov")
          .groupByAndOrderByClause("group by bov.id");
  private static final String FROM_STATEMENT = "SELECT * FROM TABLE WHERE valid_from = '%s' and valid_to = '%s';";
  private static final String WHERE_CLAUSE = "WHERE '%s' between valid_from and valid_to;";

  @Test
  void shouldReturnFromStatementQueryForWorldOnlyFutureTimetable() {
    // given
    // when
    final String query = SqlQueryUtil.getFromStatementQueryForWorldOnlyTypes(
        ExportTypeV2.WORLD_FUTURE_TIMETABLE, FROM_STATEMENT);

    // then
    final LocalDate expectedDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(FROM_STATEMENT.formatted(expectedDateAsString, expectedDateAsString));
  }

  @Test
  void shouldReturnFromStatementQueryForWorldOnlyFull() {
    // given
    // when
    final String query = SqlQueryUtil.getFromStatementQueryForWorldOnlyTypes(
        ExportTypeV2.WORLD_FULL, FROM_STATEMENT);

    // then
    final LocalDate expectedDate = LocalDate.now();
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(FROM_STATEMENT.formatted(expectedDateAsString, expectedDateAsString));
  }

  @Test
  void shouldReturnFromStatementQueryForWorldOnlyActual() {
    // given
    // when
    final String query = SqlQueryUtil.getFromStatementQueryForWorldOnlyTypes(
        ExportTypeV2.WORLD_ACTUAL, FROM_STATEMENT);

    // then
    final LocalDate expectedDate = LocalDate.now();
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(FROM_STATEMENT.formatted(expectedDateAsString, expectedDateAsString));
  }

  @Test
  void shouldReturnWhereClauseForWorldOnlyFutureTimetable() {
    // given
    // when
    final String query = SqlQueryUtil.getWhereClauseForWorldOnlyTypes(ExportTypeV2.WORLD_FUTURE_TIMETABLE, WHERE_CLAUSE);

    // then
    final LocalDate expectedDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(WHERE_CLAUSE.formatted(expectedDateAsString));
  }

  @Test
  void shouldReturnWhereClauseForWorldOnlyFull() {
    // given
    // when
    final String query = SqlQueryUtil.getWhereClauseForWorldOnlyTypes(ExportTypeV2.WORLD_FULL, WHERE_CLAUSE);

    // then
    assertThat(query).isEmpty();
  }

  @Test
  void shouldReturnWhereClauseForWorldOnlyActual() {
    // given
    // when
    final String query = SqlQueryUtil.getWhereClauseForWorldOnlyTypes(ExportTypeV2.WORLD_ACTUAL, WHERE_CLAUSE);

    // then
    final LocalDate expectedDate = LocalDate.now();
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(WHERE_CLAUSE.formatted(expectedDateAsString));
  }

  @Test
  void shouldReturnSqlStatementForFullAndContactPointVersion() {
    // given
    String select = """
        SELECT cpv.*
        FROM contact_point_version cpv
        """;
    String whereStatementContactPointVersion = "WHERE '%s' between cpv.valid_from and cpv.valid_to";
    String groupByStatement = "GROUP BY cpv.id";
    // when
    final String query = SqlQueryUtil.buildSqlQuery(select,
        SqlQueryUtil.getWhereClause(ExportTypeV2.FULL, whereStatementContactPointVersion), groupByStatement);
    final String expectedQuery = select + StringUtils.SPACE + groupByStatement + ";";

    // then
    assertThat(query.replaceAll("\\s+", " ")).isEqualTo(expectedQuery.replaceAll("\\s+", " "));
  }

  @Test
  void shouldReturnSqlStatementForActualAndContactPointVersion() {
    // given
    String select = """
        SELECT cpv.*
        FROM contact_point_version cpv
        """;
    String whereStatementContactPointVersion = "WHERE '%s' between cpv.valid_from and cpv.valid_to";
    String groupByStatement = "GROUP BY cpv.id";
    // when
    final String query = SqlQueryUtil.buildSqlQuery(select,
        SqlQueryUtil.getWhereClause(ExportTypeV2.ACTUAL, whereStatementContactPointVersion), groupByStatement);
    final LocalDate expectedDate = LocalDate.now();
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    final String expectedQuery =
        select + StringUtils.SPACE + whereStatementContactPointVersion.formatted(expectedDateAsString) + StringUtils.SPACE
            + groupByStatement + ";";

    // then
    assertThat(query).isEqualTo(expectedQuery);
  }

  @Test
  void shouldReturnSqlStatementForTimetableFutureAndContactPointVersion() {
    // given
    String select = """
        SELECT cpv.*
        FROM contact_point_version cpv
        """;
    String whereStatementContactPointVersion = "WHERE '%s' between cpv.valid_from and cpv.valid_to";
    String groupByStatement = "GROUP BY cpv.id";
    // when
    final String query = SqlQueryUtil.buildSqlQuery(select,
        SqlQueryUtil.getWhereClause(ExportTypeV2.FUTURE_TIMETABLE, whereStatementContactPointVersion), groupByStatement);
    final LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final String expectedDateAsString = DateHelper.getDateAsSqlString(futureTimeTableYearDate);
    final String expectedQuery =
        select + StringUtils.SPACE + whereStatementContactPointVersion.formatted(expectedDateAsString) + StringUtils.SPACE
            + groupByStatement + ";";

    // then
    assertThat(query).isEqualTo(expectedQuery);
  }

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
  void shouldReturnSqlStatementsForFutureTimetableBusinessOrganisation() {
    String sqlQuery = EXPORT_BUSINESS_ORGANISATION_SQL_QUERY_BUILDER
        .exportType(ExportTypeV2.FUTURE_TIMETABLE)
        .build()
        .getQuery();

    String futureTimetable =
        DateHelper.getDateAsSqlString(FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
    assertThat(sqlQuery).isEqualTo("""
        select * from business_organisation bov WHERE '%s' >= valid_from AND '%s' <= valid_to group by bov.id;""".formatted(
        futureTimetable, futureTimetable));
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
