package ch.sbb.exportservice.integration.sql;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.toilet.ToiletVersion;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.job.toilet.ToiletVersionRowMapper;
import ch.sbb.exportservice.job.toilet.ToiletVersionSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class ToiletSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullToilets() throws SQLException {
    //given

    insertToilet(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.of(2000, 1, 1),
        LocalDate.of(2099, 12, 31));
    String sqlQuery = ToiletVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<ToiletVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualToilets() throws SQLException {
    //given
    insertToilet(2, "ch:1:sloid:7001:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), LocalDate.now(),
        LocalDate.now());

    String sqlQuery = ToiletVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<ToiletVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableFutureToilets() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertToilet(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000),
        actualTimetableYearChangeDate.minusYears(1),
        actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = ToiletVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<ToiletVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<ToiletVersion> executeQuery(String sqlQuery) throws SQLException {
    List<ToiletVersion> result = new ArrayList<>();
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      ToiletVersionRowMapper toiletVersionRowMapper = new ToiletVersionRowMapper();
      while (resultSet.next()) {
        ToiletVersion toiletVersion = toiletVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(toiletVersion);
      }
    }
    connection.close();
    return result;
  }

}
