package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.parkinglot.sql.ParkingLotVersionRowMapper;
import ch.sbb.exportservice.job.prm.parkinglot.sql.ParkingLotVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ParkingLotSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullParkingLots() throws SQLException {
    //given

    insertParkingLot(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.of(2000, 1, 1),
        LocalDate.of(2099, 12, 31));
    String sqlQuery = ParkingLotVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<ParkingLotVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualParkingLots() throws SQLException {
    //given
    insertParkingLot(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.now(),
        LocalDate.now());

    String sqlQuery = ParkingLotVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<ParkingLotVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableYears() throws SQLException {
    // given
    final LocalDate now = LocalDate.now();

    insertParkingLot(2, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), now, now);
    insertParkingLot(20, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), now.minusMonths(5),
        now.minusMonths(4));
    insertParkingLot(200, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), now.plusMonths(4),
        now.plusMonths(5));
    insertParkingLot(2000, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.of(1999, 1, 1),
        LocalDate.of(2010, 1, 1));

    final String sqlQuery = ParkingLotVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    // when
    final List<ParkingLotVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty().hasSize(3);
    assertThat(result.getFirst().getParentServicePointSloid()).isEqualTo("ch:1:sloid:7000");
  }

  @Test
  void shouldReturnTimetableFutureParkingLots() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertParkingLot(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000),
        actualTimetableYearChangeDate.minusYears(1),
        actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = ParkingLotVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<ParkingLotVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<ParkingLotVersion> executeQuery(String sqlQuery) throws SQLException {
    List<ParkingLotVersion> result = new ArrayList<>();
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      ParkingLotVersionRowMapper parkingLotVersionRowMapper = new ParkingLotVersionRowMapper();
      while (resultSet.next()) {
        ParkingLotVersion servicePointVersion = parkingLotVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(servicePointVersion);
      }
    }
    connection.close();
    return result;
  }

}
