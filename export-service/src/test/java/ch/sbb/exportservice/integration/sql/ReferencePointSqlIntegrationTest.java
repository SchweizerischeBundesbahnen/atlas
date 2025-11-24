package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.referencepoint.entity.ReferencePointVersion;
import ch.sbb.exportservice.job.prm.referencepoint.sql.ReferencePointVersionRowMapper;
import ch.sbb.exportservice.job.prm.referencepoint.sql.ReferencePointVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReferencePointSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullReferencePoints() throws SQLException {
    //given

    insertReferencePoint(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.of(2000, 1, 1),
        LocalDate.of(2099, 12, 31));
    String sqlQuery = ReferencePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<ReferencePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualReferencePoints() throws SQLException {
    //given
    insertReferencePoint(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.now(),
        LocalDate.now());

    String sqlQuery = ReferencePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<ReferencePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableYears() throws SQLException {
    // given
    final LocalDate now = LocalDate.now();

    insertReferencePoint(2, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), now, now);
    insertReferencePoint(20, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), now.minusMonths(5),
        now.minusMonths(4));
    insertReferencePoint(200, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), now.plusMonths(4),
        now.plusMonths(5));
    insertReferencePoint(2000, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000),
        LocalDate.of(1999, 1, 1),
        LocalDate.of(2010, 1, 1));

    final String sqlQuery = ReferencePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    // when
    final List<ReferencePointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty().hasSize(3);
    assertThat(result.getFirst().getParentServicePointSloid()).isEqualTo("ch:1:sloid:7000");
  }

  @Test
  void shouldReturnTimetableFutureReferencePoints() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertReferencePoint(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000),
        actualTimetableYearChangeDate.minusYears(1),
        actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = ReferencePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<ReferencePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<ReferencePointVersion> executeQuery(String sqlQuery) throws SQLException {
    List<ReferencePointVersion> result = new ArrayList<>();
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      ReferencePointVersionRowMapper referencePointVersionRowMapper = new ReferencePointVersionRowMapper();
      while (resultSet.next()) {
        ReferencePointVersion servicePointVersion = referencePointVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(servicePointVersion);
      }
    }
    connection.close();
    return result;
  }

}
