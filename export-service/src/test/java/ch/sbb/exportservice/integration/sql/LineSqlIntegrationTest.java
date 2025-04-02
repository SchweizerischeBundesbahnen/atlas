package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.line.entity.Line;
import ch.sbb.exportservice.job.lidi.line.sql.LineRowMapper;
import ch.sbb.exportservice.job.lidi.line.sql.LineSqlQueryUtil;
import ch.sbb.exportservice.job.lidi.line.entity.Line.LineBuilder;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.util.ExportYearsTimetableUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LineSqlIntegrationTest extends BaseLiDiSqlIntegrationTest {

  @Test
  void shouldReturnFullLines() throws SQLException {
    //given
    Line line = Line.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000")
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);

    String sqlQuery = LineSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<Line> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnActualLines() throws SQLException {
    //given
    Line line = Line.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now())
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);

    String sqlQuery = LineSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<Line> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableFutureLines() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());

    Line line = Line.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000")
        .validFrom(actualTimetableYearChangeDate.minusYears(1))
        .validTo(actualTimetableYearChangeDate.plusYears(1))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);
    String sqlQuery = LineSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<Line> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  //  @formatter:off
  /**
   *               |---------Timetable Years Range---------|
   *    |----| Line 1: Before Date range -> X
   *               | Line 2: Valid Only On Range Start -> Exported
   *                                                       | Line 3: Valid Only On Range End -> Exported
   *          |---------------| Line 4: Valid over range start -> Exported
   *                                              |---------------| Line 5: Valid over range end -> Exported
   *           |---------------------------------------------------| Line 6: Valid over total range -> Exported
   *                                                             |------| Line 7: Valid After range -> X
   */
  //  @formatter:on
  @Test
  void shouldReturnTimetableYearsLines() throws SQLException {
    //given
    DateRange timetableYearsDateRange = ExportYearsTimetableUtil.getTimetableYearsDateRange();

    Line line = lineBuilder(1)
        .validFrom(timetableYearsDateRange.getFrom().minusDays(2))
        .validTo(timetableYearsDateRange.getFrom().minusDays(1))
        .build();
    insertLineVersion(line);

    line = lineBuilder(2)
        .validFrom(timetableYearsDateRange.getFrom())
        .validTo(timetableYearsDateRange.getFrom())
        .build();
    insertLineVersion(line);

    line = lineBuilder(3)
        .validFrom(timetableYearsDateRange.getTo())
        .validTo(timetableYearsDateRange.getTo())
        .build();
    insertLineVersion(line);

    line = lineBuilder(4)
        .validFrom(timetableYearsDateRange.getFrom().minusDays(7))
        .validTo(timetableYearsDateRange.getFrom().plusDays(10))
        .build();
    insertLineVersion(line);

    line = lineBuilder(5)
        .validFrom(timetableYearsDateRange.getTo().minusDays(12))
        .validTo(timetableYearsDateRange.getTo().plusDays(15))
        .build();
    insertLineVersion(line);

    line = lineBuilder(6)
        .validFrom(timetableYearsDateRange.getFrom().minusDays(18))
        .validTo(timetableYearsDateRange.getTo().plusDays(20))
        .build();
    insertLineVersion(line);

    line = lineBuilder(7)
        .validFrom(timetableYearsDateRange.getTo().plusDays(2))
        .validTo(timetableYearsDateRange.getTo().plusDays(5))
        .build();
    insertLineVersion(line);

    String sqlQuery = LineSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    //when
    List<Line> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(5);
    assertThat(result).extracting(Line::getId).containsExactly(2L, 3L, 4L, 5L, 6L);
  }

  private LineBuilder<?, ?> lineBuilder(int id){
    return Line.builder()
        .id((long) id)
        .slnid("ch:1:slnid:10000"+id)
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .swissLineNumber("r.0"+id)
        .description("Linie "+id)
        .number(String.valueOf(id))
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011");
  }

  private List<Line> executeQuery(String sqlQuery) throws SQLException {
    List<Line> result = new ArrayList<>();
    Connection connection = lineDirectoryDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      LineRowMapper lineRowMapper = new LineRowMapper();
      while (resultSet.next()) {
        Line line = lineRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(line);
      }
    }
    connection.close();
    return result;
  }

}
