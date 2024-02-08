package ch.sbb.exportservice.reader;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiExportType;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SqlQueryUtilTest {

  private static final String FROM_STATEMENT = "SELECT * FROM TABLE WHERE valid_from = '%s' and valid_to = '%s';";
  private static final String WHERE_CLAUSE = "WHERE '%s' between valid_from and valid_to;";

  @Test
  void shouldReturnFromStatementQueryForWorldOnlyFutureTimetable() {
    // given
    // when
    final String query = SqlQueryUtil.getFromStatementQueryForWorldOnlyTypes(
        SePoDiExportType.WORLD_ONLY_TIMETABLE_FUTURE, FROM_STATEMENT);

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
        SePoDiExportType.WORLD_FULL, FROM_STATEMENT);

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
        SePoDiExportType.WORLD_ONLY_ACTUAL, FROM_STATEMENT);

    // then
    final LocalDate expectedDate = LocalDate.now();
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(FROM_STATEMENT.formatted(expectedDateAsString, expectedDateAsString));
  }

  @Test
  void shouldReturnWhereClauseForWorldOnlyFutureTimetable() {
    // given
    // when
    final String query = SqlQueryUtil.getWhereClauseForWorldOnlyTypes(SePoDiExportType.WORLD_ONLY_TIMETABLE_FUTURE, WHERE_CLAUSE);

    // then
    final LocalDate expectedDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(WHERE_CLAUSE.formatted(expectedDateAsString));
  }

  @Test
  void shouldReturnWhereClauseForWorldOnlyFull() {
    // given
    // when
    final String query = SqlQueryUtil.getWhereClauseForWorldOnlyTypes(SePoDiExportType.WORLD_FULL, WHERE_CLAUSE);

    // then
    assertThat(query).isEqualTo("");
  }

  @Test
  void shouldReturnWhereClauseForWorldOnlyActual() {
    // given
    // when
    final String query = SqlQueryUtil.getWhereClauseForWorldOnlyTypes(SePoDiExportType.WORLD_ONLY_ACTUAL, WHERE_CLAUSE);

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
    final String query = SqlQueryUtil.buildSqlQuery(select, SqlQueryUtil.getWhereClause(PrmExportType.FULL, whereStatementContactPointVersion), groupByStatement);

    // then
    assertThat(query).isEqualTo(select + StringUtils.SPACE + groupByStatement + ";");
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
    final String query = SqlQueryUtil.buildSqlQuery(select, SqlQueryUtil.getWhereClause(PrmExportType.ACTUAL, whereStatementContactPointVersion), groupByStatement);

    // then
    final LocalDate expectedDate = LocalDate.now();
    final String expectedDateAsString = DateHelper.getDateAsSqlString(expectedDate);
    assertThat(query).isEqualTo(select + StringUtils.SPACE + whereStatementContactPointVersion.formatted(expectedDateAsString) + StringUtils.SPACE + groupByStatement + ";");
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
    final String query = SqlQueryUtil.buildSqlQuery(select, SqlQueryUtil.getWhereClause(PrmExportType.TIMETABLE_FUTURE, whereStatementContactPointVersion), groupByStatement);

    // then
    final LocalDate futureTimeTableYearDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final String expectedDateAsString = DateHelper.getDateAsSqlString(futureTimeTableYearDate);
    assertThat(query).isEqualTo(select + StringUtils.SPACE + whereStatementContactPointVersion.formatted(expectedDateAsString) + StringUtils.SPACE + groupByStatement + ";");
  }

}
