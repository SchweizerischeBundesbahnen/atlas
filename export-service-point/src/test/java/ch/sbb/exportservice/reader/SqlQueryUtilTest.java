package ch.sbb.exportservice.reader;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.versioning.date.DateHelper;
import ch.sbb.exportservice.model.SePoDiExportType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

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

}
