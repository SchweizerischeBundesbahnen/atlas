package ch.sbb.exportservice.integration.sql;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.relation.RelationVersion;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.job.prm.relation.RelationVersionRowMapper;
import ch.sbb.exportservice.job.prm.relation.RelationVersionSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class RelationSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  @Test
  void shouldReturnFullRelation() throws SQLException {
    //given

    insertRelation(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), "ch:1:sloid:7000:2",
        LocalDate.of(2000, 1, 1), LocalDate.of(2099, 12, 31));
    String sqlQuery = RelationVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FULL);

    //when
    List<RelationVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnActualRelation() throws SQLException {
    //given
    insertRelation(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), "ch:1:sloid:7000:2",
        LocalDate.now(), LocalDate.now());

    String sqlQuery = RelationVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.ACTUAL);

    //when
    List<RelationVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  @Test
  void shouldReturnTimetableFutureRelation() throws SQLException {
    //given
    LocalDate actualTimetableYearChangeDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    insertRelation(1, "ch:1:sloid:7000:1", ServicePointNumber.ofNumberWithoutCheckDigit(8507000), "ch:1:sloid:7000:2",
        actualTimetableYearChangeDate.minusYears(1), actualTimetableYearChangeDate.plusYears(1));
    String sqlQuery = RelationVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.FUTURE_TIMETABLE);

    //when
    List<RelationVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);

  }

  private List<RelationVersion> executeQuery(String sqlQuery) throws SQLException {
    List<RelationVersion> result = new ArrayList<>();
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      RelationVersionRowMapper relationVersionRowMapper = new RelationVersionRowMapper();
      while (resultSet.next()) {
        RelationVersion servicePointVersion = relationVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(servicePointVersion);
      }
    }
    connection.close();
    return result;
  }

}
