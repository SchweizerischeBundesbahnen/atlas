package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.stoppoint.sql.StopPointVersionRowMapper;
import ch.sbb.exportservice.job.prm.stoppoint.sql.StopPointVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StopPointSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullStopPoints() throws SQLException {
    //given

    insertStopPoint(1000, 8507000, "ch:1:sloid:70000", LocalDate.of(2000, 1, 1), LocalDate.of(2099, 12, 31));
    String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<StopPointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualStopPoints() throws SQLException {
    //given
    insertStopPoint(1000, 8507000, "ch:1:sloid:70000", LocalDate.now(), LocalDate.now());

    String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<StopPointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableYears() throws SQLException {
    // given
    final LocalDate now = LocalDate.now();

    insertStopPoint(2, 8507000, "ch:1:sloid:7001:1", now, now);
    insertStopPoint(20, 8507000, "ch:1:sloid:7001:1", now.minusMonths(5),
        now.minusMonths(4));
    insertStopPoint(200, 8507000, "ch:1:sloid:7001:1", now.plusMonths(4),
        now.plusMonths(5));
    insertStopPoint(2000, 8507000, "ch:1:sloid:7001:1",
        LocalDate.of(1999, 1, 1),
        LocalDate.of(2010, 1, 1));

    final String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    // when
    final List<StopPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty().hasSize(3);
  }

  @Test
  void shouldReturnTimetableFutureStopPoints() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertStopPoint(1000, 8507000, "ch:1:sloid:70000", actualTimetableYearChangeDate.minusYears(1),
        actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<StopPointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<StopPointVersion> executeQuery(String sqlQuery) throws SQLException {
    List<StopPointVersion> result = new ArrayList<>();
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      StopPointVersionRowMapper stopPointVersionRowMapper = new StopPointVersionRowMapper();
      while (resultSet.next()) {
        StopPointVersion servicePointVersion = stopPointVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(servicePointVersion);
      }
    }
    connection.close();
    return result;
  }

}
