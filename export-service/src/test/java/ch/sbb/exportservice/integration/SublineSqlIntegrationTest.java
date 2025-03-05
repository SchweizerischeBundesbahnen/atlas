package ch.sbb.exportservice.integration;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.entity.lidi.Line;
import ch.sbb.exportservice.entity.lidi.Subline;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.reader.SublineRowMapper;
import ch.sbb.exportservice.reader.SublineSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SublineSqlIntegrationTest extends BaseLiDiSqlIntegrationTest {

  private static final String MAIN_LINE_SLNID = "ch:1:slnid:100000";

  @BeforeEach
  void setUp() throws SQLException {
    Line line = Line.builder()
        .id(1000L)
        .slnid(MAIN_LINE_SLNID)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(line);
  }

  @AfterEach
  void tearDown() throws SQLException {
    cleanupLines();
  }

  @Test
  void shouldReturnFullSublines() throws SQLException {
    //given
    Subline subline = Subline.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000:1")
        .mainlineSlnid(MAIN_LINE_SLNID)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .status(Status.VALIDATED)
        .sublineType(SublineType.OPERATIONAL)
        .concessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .swissSublineNumber("r.01.1")
        .description("Linie 1a")
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertSublineVersion(subline);

    String sqlQuery = SublineSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<Subline> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnActualSublines() throws SQLException {
    //given
    Subline subline = Subline.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000:1")
        .mainlineSlnid(MAIN_LINE_SLNID)
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now())
        .status(Status.VALIDATED)
        .sublineType(SublineType.OPERATIONAL)
        .concessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .swissSublineNumber("r.01.1")
        .description("Linie 1a")
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertSublineVersion(subline);

    String sqlQuery = SublineSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<Subline> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnTimetableFutureSublines() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());

    Subline subline = Subline.builder()
        .id(1L)
        .slnid("ch:1:slnid:100000:1")
        .mainlineSlnid(MAIN_LINE_SLNID)
        .validFrom(actualTimetableYearChangeDate.minusYears(1))
        .validTo(actualTimetableYearChangeDate.plusYears(1))
        .status(Status.VALIDATED)
        .sublineType(SublineType.OPERATIONAL)
        .concessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .swissSublineNumber("r.01.1")
        .description("Linie 1a")
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertSublineVersion(subline);
    String sqlQuery = SublineSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<Subline> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<Subline> executeQuery(String sqlQuery) throws SQLException {
    List<Subline> result = new ArrayList<>();
    Connection connection = lineDirectoryDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      SublineRowMapper sublineRowMapper = new SublineRowMapper();
      while (resultSet.next()) {
        Subline subline = sublineRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(subline);
      }
    }
    connection.close();
    return result;
  }

}
