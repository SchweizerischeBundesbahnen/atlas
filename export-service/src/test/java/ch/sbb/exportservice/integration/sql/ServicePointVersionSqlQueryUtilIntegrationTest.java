package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.sql.ServicePointVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.servicepoint.sql.ServicePointVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicePointVersionSqlQueryUtilIntegrationTest extends BaseSqlIntegrationTest {

  @Test
  void shouldReturnWorldOnlyActualWithActualBusinessOrganisationData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 4105886;
    insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
    String sboid = "ch:1:sboid:101999";
    insertSharedBusinessOrganisation(sboid, "abb", now, now);
    insertSharedBusinessOrganisation(sboid, "abbIt", now.plusMonths(1), now.plusMonths(2));
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumber);
    assertThat(result.getFirst().getSharedBusinessOrganisation().getBusinessOrganisation()).isEqualTo(sboid);
    assertThat(result.getFirst().getSharedBusinessOrganisation().getBusinessOrganisationAbbreviationIt()).isEqualTo("abb");
  }

  @Test
  void shouldReturnWorldOnlyActualWithoutBusinessOrganisationData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 4105886;
    insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
    String sboid = "ch:1:sboid:101999";
    insertSharedBusinessOrganisation(sboid, "abb", now.minusMonths(2), now.minusMonths(1));
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumber);
    assertThat(result.getFirst().getSharedBusinessOrganisation().getBusinessOrganisation()).isEqualTo(sboid);
    assertThat(result.getFirst().getSharedBusinessOrganisation().getBusinessOrganisationAbbreviationIt()).isNull();
  }

  @Test
  void shouldReturnWorldFullData() throws SQLException {
    //given
    final LocalDate now = LocalDate.now();
    insertServicePoint(4156734, now, now, Country.ALBANIA);
    insertServicePoint(6847382, now.minusMonths(5), now.minusMonths(4), Country.AFGHANISTAN);
    insertServicePoint(8547389, now.plusMonths(4), now.plusMonths(5), Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_FULL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(3);
  }

  @Test
  void shouldReturnWorldOnlyActualData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 4105886;
    insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  void shouldReturnWorldOnlyTimetableYearsData() throws SQLException {
    //given
    LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    int servicePointNumber = 9005886;
    insertServicePoint(servicePointNumber, now, now, Country.EGYPT);
    insertServicePoint(5786587, LocalDate.of(2000, Month.JANUARY, 1), LocalDate.of(2020, Month.JANUARY, 1), Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_TIMETABLE_YEARS);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  void shouldReturnSwissOnlyTimetableYearsData() throws SQLException {
    //given
    LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    int servicePointNumber = 9005886;
    insertServicePoint(servicePointNumber, now, now, Country.SWITZERLAND);
    insertServicePoint(5786587, LocalDate.of(2000, Month.JANUARY, 1), LocalDate.of(2020, Month.JANUARY, 1), Country.SWITZERLAND);
    insertServicePoint(9005999, now, now, Country.EGYPT);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.SWISS_TIMETABLE_YEARS);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  void shouldReturnSwissOnlyActualData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 8572299;
    insertServicePoint(servicePointNumber, now, now, Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.SWISS_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  void shouldReturnSwissOnlyFullData() throws SQLException {
    //given
    final LocalDate now = LocalDate.now();
    int servicePointNumberAfghanistan = 6805886;
    insertServicePoint(servicePointNumberAfghanistan, now, now, Country.AFGHANISTAN);
    int servicePointNumberSwitzerland = 8572299;
    insertServicePoint(servicePointNumberSwitzerland, now.minusMonths(5), now.minusMonths(4), Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.SWISS_FULL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNumber().getValue()).isEqualTo(servicePointNumberSwitzerland);
  }

  @Test
  void shouldReturnGlobalIdOfServicePoint() throws SQLException {
    // Given
    final LocalDate now = LocalDate.now();
    int servicePointNumber = 8572299;
    insertServicePoint(servicePointNumber, now.minusMonths(5), now.minusMonths(4), Country.SWITZERLAND);
    insertServicePointGlobalId(servicePointNumber, "ch:1:sloid:1200001");
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.SWISS_FULL);

    // When
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getGlobalId()).isEqualTo("ch:1:sloid:1200001");
  }

  @Test
  void shouldReturnNullGlobalIdWhenServicePointHasNone() throws SQLException {
    // Given
    final LocalDate now = LocalDate.now();
    int servicePointNumber = 8572299;
    insertServicePoint(servicePointNumber, now.minusMonths(5), now.minusMonths(4), Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.SWISS_FULL);

    // When
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getGlobalId()).isNull();
  }

  private List<ServicePointVersion> executeQuery(String sqlQuery) throws SQLException {
    List<ServicePointVersion> result = new ArrayList<>();
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      ServicePointVersionRowMapper servicePointVersionRowMapper = new ServicePointVersionRowMapper();
      while (resultSet.next()) {
        ServicePointVersion servicePointVersion = servicePointVersionRowMapper.mapRow(resultSet, resultSet.getRow());
        result.add(servicePointVersion);
      }
    }
    connection.close();
    return result;
  }

}
