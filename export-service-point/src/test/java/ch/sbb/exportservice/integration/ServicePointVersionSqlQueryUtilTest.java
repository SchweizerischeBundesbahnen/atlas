package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.reader.ServicePointVersionRowMapper;
import ch.sbb.exportservice.reader.ServicePointVersionSqlQueryUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ServicePointVersionSqlQueryUtilTest extends BaseSqlTest {

  @Test
  public void shouldReturnWorldOnlyActualWithActualBusinessOrganisationData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 19058867;
    insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
    String sboid = "ch:1:sboid:101999";
    insertSharedBusinessOrganisation(sboid, "abb", now, now);
    insertSharedBusinessOrganisation(sboid, "abbIt", now.plusMonths(1), now.plusMonths(2));
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
    assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisation()).isEqualTo(sboid);
    assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisationAbbreviationIt()).isEqualTo("abb");
  }

  @Test
  public void shouldReturnWorldOnlyActualWithoutBusinessOrganisationData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 19058867;
    insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
    String sboid = "ch:1:sboid:101999";
    insertSharedBusinessOrganisation(sboid, "abb", now.minusMonths(2), now.minusMonths(1));
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
    assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisation()).isEqualTo(sboid);
    assertThat(result.get(0).getBusinessOrganisation().getBusinessOrganisationAbbreviationIt()).isEqualTo(null);
  }

  @Test
  public void shouldReturnWorldFullData() throws SQLException {
    //given
    final LocalDate now = LocalDate.now();
    insertServicePoint(19567343, now, now, Country.ALBANIA);
    insertServicePoint(78473823, now.minusMonths(5), now.minusMonths(4), Country.AFGHANISTAN);
    insertServicePoint(85473894, now.plusMonths(4), now.plusMonths(5), Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_FULL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(3);
  }

  @Test
  public void shouldReturnWorldOnlyActualData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 19058867;
    insertServicePoint(servicePointNumber, now, now, Country.ALBANIA);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  public void shouldReturnWorldOnlyTimetableFutureData() throws SQLException {
    //given
    LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    int servicePointNumber = 19058867;
    insertServicePoint(servicePointNumber, now, now, Country.EGYPT);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_TIMETABLE_FUTURE);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  public void shouldReturnSwissOnlyTimetableFutureData() throws SQLException {
    //given
    LocalDate now = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    int servicePointNumberAfghanistan = 68058867;
    insertServicePoint(servicePointNumberAfghanistan, now, now, Country.AFGHANISTAN);
    int servicePointNumberSwitzerland = 85722999;
    insertServicePoint(servicePointNumberSwitzerland, now, now, Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.SWISS_ONLY_TIMETABLE_FUTURE);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumberSwitzerland);
  }

  @Test
  public void shouldReturnSwissOnlyActualData() throws SQLException {
    //given
    LocalDate now = LocalDate.now();
    int servicePointNumber = 85722999;
    insertServicePoint(servicePointNumber, now, now, Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.SWISS_ONLY_ACTUAL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumber);
  }

  @Test
  public void shouldReturnSwissOnlyFullData() throws SQLException {
    //given
    final LocalDate now = LocalDate.now();
    int servicePointNumberAfghanistan = 68058867;
    insertServicePoint(servicePointNumberAfghanistan, now, now, Country.AFGHANISTAN);
    int servicePointNumberSwitzerland = 85722999;
    insertServicePoint(servicePointNumberSwitzerland, now.minusMonths(5), now.minusMonths(4), Country.SWITZERLAND);
    String sqlQuery = ServicePointVersionSqlQueryUtil.getSqlQuery(ExportType.SWISS_ONLY_FULL);

    //when
    List<ServicePointVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getNumber().getValue()).isEqualTo(servicePointNumberSwitzerland);
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
