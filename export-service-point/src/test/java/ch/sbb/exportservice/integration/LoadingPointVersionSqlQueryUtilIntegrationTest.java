package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.entity.LoadingPointVersion;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.reader.LoadingPointVersionRowMapper;
import ch.sbb.exportservice.reader.LoadingPointVersionSqlQueryUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LoadingPointVersionSqlQueryUtilIntegrationTest extends BaseSqlIntegrationTest {

  @Test
  void shouldReturnFullWorld() throws SQLException {
    // given
    final LocalDate now = LocalDate.now();
    final int servicePointNumber = 8509111;
    final String sboid = "ch:1:sboid:101999";
    insertServicePoint(servicePointNumber, now, now, Country.AUSTRIA);
    insertSharedBusinessOrganisation(sboid, "testIt", now, now);
    insertLoadingPoint(50, servicePointNumber, now.minusMonths(5), now.minusMonths(4));
    insertLoadingPoint(60, servicePointNumber, now, now);
    insertLoadingPoint(70, servicePointNumber, now.plusMonths(4), now.plusMonths(5));
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_FULL);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(3);
    result.sort(Comparator.comparing(LoadingPointVersion::getNumber));
    assertThat(result.get(0).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(0).getServicePointBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
    assertThat(result.get(1).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(1).getServicePointBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
    assertThat(result.get(2).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(2).getServicePointBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
  }

  @Test
  void shouldReturnActualDate() throws SQLException {
    // given
    final LocalDate now = LocalDate.now();
    final int servicePointNumber = 8509111;
    final String sboid = "ch:1:sboid:101999";
    insertServicePoint(servicePointNumber, now, now, Country.AFGHANISTAN);
    insertSharedBusinessOrganisation(sboid, "testIt", now, now);
    insertLoadingPoint(50, servicePointNumber, now.minusMonths(5), now.minusMonths(4));
    insertLoadingPoint(60, servicePointNumber, now, now);
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_ACTUAL);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(isDateInRange(now, result.get(0).getValidFrom(), result.get(0).getValidTo())).isTrue();
    assertThat(result.get(0).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(0).getServicePointBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
  }

  @Test
  void shouldReturnFutureTimetableDateWithMatchingLoadingPointAndSePoBo() throws SQLException {
    // given
    final LocalDate futureDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final int servicePointNumber = 8509111;
    final String sboid = "ch:1:sboid:101999";
    insertServicePoint(servicePointNumber, futureDate, futureDate, Country.AFGHANISTAN);
    insertSharedBusinessOrganisation(sboid, "testIt", futureDate, futureDate);
    insertLoadingPoint(50, servicePointNumber, futureDate, futureDate);
    insertLoadingPoint(60, servicePointNumber, futureDate.minusMonths(5), futureDate.minusMonths(4));
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_TIMETABLE_FUTURE);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(isDateInRange(futureDate, result.get(0).getValidFrom(), result.get(0).getValidTo())).isTrue();
    assertThat(result.get(0).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(0).getServicePointBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
  }

  @Test
  void shouldReturnFutureTimetableDateWithMatchingLoadingPointWithoutSePoBo() throws SQLException {
    // given
    final LocalDate futureDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final int servicePointNumber = 8509111;
    final String sboid = "ch:1:sboid:101999";
    insertServicePoint(servicePointNumber, futureDate.minusMonths(5), futureDate.minusMonths(4), Country.AFGHANISTAN);
    insertSharedBusinessOrganisation(sboid, "testIt", futureDate.minusMonths(5), futureDate.minusMonths(4));
    insertLoadingPoint(50, servicePointNumber, futureDate, futureDate);
    insertLoadingPoint(60, servicePointNumber, futureDate.minusMonths(5), futureDate.minusMonths(4));
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_TIMETABLE_FUTURE);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(isDateInRange(futureDate, result.get(0).getValidFrom(), result.get(0).getValidTo())).isTrue();
    assertThat(result.get(0).getParentSloidServicePoint()).isEqualTo(null);
    assertThat(result.get(0).getServicePointBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(null);
  }

  @Test
  void shouldReturnFutureTimetableDateWithoutMatchingLoadingPointWithoutSePoBo() throws SQLException {
    // given
    final LocalDate futureDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final int servicePointNumber = 8509111;
    final String sboid = "ch:1:sboid:101999";
    insertServicePoint(servicePointNumber, futureDate.minusMonths(5), futureDate.minusMonths(4), Country.AFGHANISTAN);
    insertSharedBusinessOrganisation(sboid, "testIt", futureDate.minusMonths(5), futureDate.minusMonths(4));
    insertLoadingPoint(60, servicePointNumber, futureDate.minusMonths(5), futureDate.minusMonths(4));
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportType.WORLD_ONLY_TIMETABLE_FUTURE);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isEmpty();
  }

  private List<LoadingPointVersion> executeQuery(String sqlQuery) throws SQLException {
    final List<LoadingPointVersion> result = new ArrayList<>();
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      final ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      final LoadingPointVersionRowMapper mapper = new LoadingPointVersionRowMapper();
      while (resultSet.next()) {
        LoadingPointVersion loadingPointVersion = mapper.mapRow(resultSet, resultSet.getRow());
        result.add(loadingPointVersion);
      }
    }
    connection.close();
    return result;
  }

  private void insertLoadingPoint(int number, int servicePointNumber, LocalDate validFrom, LocalDate validTo)
      throws SQLException {
    final String insertSql = """
        insert into loading_point_version (id, number, designation, designation_long, connection_point,
        service_point_number, valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        values (nextval('loading_point_version_seq'), %d, 'Ladestelle', 'Ladestelle Lang', true, %d,
        '%s', '%s', '2020-05-18 12:43:34.000000', 'fs45117', '2020-05-18 12:43:34.000000', 'fs45117', 0);
        """
        .formatted(number, servicePointNumber, formatDate(validFrom), formatDate(validTo));
    final Connection connection = servicePointDataSource.getConnection();
    try (final PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

}
