package ch.sbb.exportservice.integration.sql;

import static ch.sbb.atlas.model.FutureTimetableHelper.getTimetableYearChangeDateToExportData;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.job.sepodi.loadingpoint.entity.LoadingPointVersion;
import ch.sbb.exportservice.job.sepodi.loadingpoint.sql.LoadingPointVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.loadingpoint.sql.LoadingPointVersionSqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoadingPointVersionSqlQueryUtilIntegrationTest extends BaseSqlIntegrationTest {

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
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_FULL);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty().hasSize(3);
    result.sort(Comparator.comparing(LoadingPointVersion::getNumber));
    assertThat(result.getFirst().getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.getFirst().getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
    assertThat(result.get(1).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(1).getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
    assertThat(result.get(2).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(2).getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
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
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_ACTUAL);

    // when
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty().hasSize(1);
    assertThat(isDateInRange(now, result.getFirst().getValidFrom(), result.getFirst().getValidTo())).isTrue();
    assertThat(result.getFirst().getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.getFirst().getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
  }

  @Test
  void shouldReturnTimetableYears() throws SQLException {
    // given
    final LocalDate timetableYearChangeDateToExportData = getTimetableYearChangeDateToExportData(LocalDate.now());
    final LocalDate lastTimetableYearChangeDateToExportData = timetableYearChangeDateToExportData.minusYears(1);
    final LocalDate nextTimetableYearChangeDateToExportData = timetableYearChangeDateToExportData.plusYears(1);
    final int servicePointNumber = 8509111;
    final String sboid = "ch:1:sboid:101999";
    insertServicePoint(servicePointNumber, lastTimetableYearChangeDateToExportData, nextTimetableYearChangeDateToExportData,
        Country.AUSTRIA);
    insertSharedBusinessOrganisation(sboid, "testIt", lastTimetableYearChangeDateToExportData,
        nextTimetableYearChangeDateToExportData);
    insertLoadingPoint(50, servicePointNumber, timetableYearChangeDateToExportData.minusMonths(5),
        timetableYearChangeDateToExportData.minusMonths(4));
    insertLoadingPoint(60, servicePointNumber, timetableYearChangeDateToExportData, timetableYearChangeDateToExportData);
    insertLoadingPoint(70, servicePointNumber, timetableYearChangeDateToExportData.plusMonths(4),
        timetableYearChangeDateToExportData.plusMonths(5));
    insertLoadingPoint(80, servicePointNumber, LocalDate.of(1999, 1, 1), LocalDate.of(2010, 1, 1));

    // when
    final String sqlQuery = LoadingPointVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_TIMETABLE_YEARS);
    final List<LoadingPointVersion> result = executeQuery(sqlQuery);

    // then
    assertThat(result).isNotEmpty().hasSize(3);
    result.sort(Comparator.comparing(LoadingPointVersion::getNumber));
    assertThat(result.getFirst().getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.getFirst().getNumber()).isEqualTo(50);
    assertThat(result.getFirst().getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
    assertThat(result.get(1).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(1).getNumber()).isEqualTo(60);
    assertThat(result.get(1).getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
    assertThat(result.get(2).getParentSloidServicePoint()).isEqualTo("ch:1:sloid:1");
    assertThat(result.get(2).getNumber()).isEqualTo(70);
    assertThat(result.get(2).getServicePointSharedBusinessOrganisation().getBusinessOrganisationNumber()).isEqualTo(3065);
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
