package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.exportservice.entity.StopPointVersion;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.reader.StopPointVersionRowMapper;
import ch.sbb.exportservice.reader.StopPointVersionSqlQueryUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StopPointSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullStopPoints() throws SQLException {
    //given

    insertStopPoint(8507000, "ch:1:sloid:70000", LocalDate.of(2000,1,1),LocalDate.of(2099,12,31));
    String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(PrmExportType.FULL);

    //when
    List<StopPointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualStopPoints() throws SQLException {
    //given
    insertStopPoint(8507000, "ch:1:sloid:70000", LocalDate.now(),LocalDate.now());

    String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(PrmExportType.ACTUAL);

    //when
    List<StopPointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableFutureStopPoints() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertStopPoint(8507000, "ch:1:sloid:70000", actualTimetableYearChangeDate.minusYears(1),
        actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = StopPointVersionSqlQueryUtil.getSqlQuery(PrmExportType.TIMETABLE_FUTURE);

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
