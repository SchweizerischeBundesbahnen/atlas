package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.lidi.line.entity.Line;
import ch.sbb.exportservice.job.lidi.line.sql.LineRowMapper;
import ch.sbb.exportservice.job.lidi.line.sql.LineSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
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

  @Test
  void shouldReturnTimetableYearsLines() throws SQLException {
    //given
    Line line = Line.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000")
        .validFrom(LocalDate.of(2024, 12, 15))
        .validTo(LocalDate.of(2024, 12, 15))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie on First Years Date")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);

    line = Line.builder()
        .id(2L)
        .slnid("ch:1:slnid:100001")
        .validFrom(LocalDate.of(2026, 12, 12))
        .validTo(LocalDate.of(2026, 12, 12))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.02")
        .description("Linie on Last Years Date")
        .number("2")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);

    line = Line.builder()
        .id(3L)
        .slnid("ch:1:slnid:100002")
        .validFrom(LocalDate.of(2026, 12, 13))
        .validTo(LocalDate.of(2026, 12, 13))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.03")
        .description("Linie after Last Years Date")
        .number("3")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);

    line = Line.builder()
        .id(4L)
        .slnid("ch:1:slnid:100003")
        .validFrom(LocalDate.of(2024, 12, 14))
        .validTo(LocalDate.of(2024, 12, 14))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.04")
        .description("Linie before first Years Date")
        .number("4")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);

    String sqlQuery = LineSqlQueryUtil.getSqlQuery(ExportTypeV2.TIMETABLE_YEARS);

    //when
    List<Line> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(2);
    assertThat(result).extracting(Line::getId).containsExactly(1L, 2L);
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
