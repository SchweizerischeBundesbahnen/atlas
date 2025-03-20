package ch.sbb.exportservice.integration.sql;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.job.prm.platform.sql.PlatformVersionRowMapper;
import ch.sbb.exportservice.job.prm.platform.sql.PlatformVersionSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PlatformSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullPlatforms() throws SQLException {
    //given

    insertPlatform(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.of(2000, 1, 1),
        LocalDate.of(2099, 12, 31));
    String sqlQuery = PlatformVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<PlatformVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualPlatforms() throws SQLException {
    //given
    insertPlatform(2, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.now(),
        LocalDate.now());

    String sqlQuery = PlatformVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<PlatformVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableFuturePlatforms() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertPlatform(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000),
        actualTimetableYearChangeDate.minusYears(1),
        actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = PlatformVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<PlatformVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<PlatformVersion> executeQuery(String sqlQuery) throws SQLException {
    List<PlatformVersion> result = new ArrayList<>();
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      PlatformVersionRowMapper platformVersionRowMapper = new PlatformVersionRowMapper();
      while (resultSet.next()) {
        PlatformVersion platformVersion = platformVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(platformVersion);
      }
    }
    connection.close();
    return result;
  }

}
