package ch.sbb.exportservice.integration.sql;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.ttfn.TimetableFieldNumber;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.job.ttfn.TimetableFieldNumberRowMapper;
import ch.sbb.exportservice.job.ttfn.TimetableFieldNumberSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TimetableFieldNumberSqlIntegrationTest extends BaseLiDiSqlIntegrationTest {

  @Test
  void shouldReturnFullTimetableFieldNumbers() throws SQLException {
    //given
    TimetableFieldNumber timetableFieldNumber = TimetableFieldNumber.builder()
        .id(1L)
        .description("description")
        .number("number")
        .ttfnid("ch:1:ttfnid:123")
        .swissTimetableFieldNumber("sttfn")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020,1,1))
        .validTo(LocalDate.of(2020,12,31))
        .businessOrganisation("ch:1:sboid:100000")
        .comment("comment")
        .build();
    insertTtfnVersion(timetableFieldNumber);

    String sqlQuery = TimetableFieldNumberSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<TimetableFieldNumber> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnActualTimetableFieldNumbers() throws SQLException {
    //given
    TimetableFieldNumber timetableFieldNumber = TimetableFieldNumber.builder()
        .id(1L)
        .description("description")
        .number("number")
        .ttfnid("ch:1:ttfnid:123")
        .swissTimetableFieldNumber("sttfn")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now())
        .businessOrganisation("ch:1:sboid:100000")
        .comment("comment")
        .build();
    insertTtfnVersion(timetableFieldNumber);

    String sqlQuery = TimetableFieldNumberSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<TimetableFieldNumber> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnTimetableFutureTimetableFieldNumbers() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());

    TimetableFieldNumber timetableFieldNumber = TimetableFieldNumber.builder()
        .id(1L)
        .description("description")
        .number("number")
        .ttfnid("ch:1:ttfnid:123")
        .swissTimetableFieldNumber("sttfn")
        .status(Status.VALIDATED)
        .validFrom(actualTimetableYearChangeDate.minusYears(1))
        .validTo(actualTimetableYearChangeDate.plusYears(1))
        .businessOrganisation("ch:1:sboid:100000")
        .comment("comment")
        .build();
    insertTtfnVersion(timetableFieldNumber);

    String sqlQuery = TimetableFieldNumberSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<TimetableFieldNumber> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<TimetableFieldNumber> executeQuery(String sqlQuery) throws SQLException {
    List<TimetableFieldNumber> result = new ArrayList<>();
    Connection connection = lineDirectoryDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      TimetableFieldNumberRowMapper timetableFieldNumberRowMapper = new TimetableFieldNumberRowMapper();
      while (resultSet.next()) {
        TimetableFieldNumber timetableFieldNumber = timetableFieldNumberRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(timetableFieldNumber);
      }
    }
    connection.close();
    return result;
  }

}
