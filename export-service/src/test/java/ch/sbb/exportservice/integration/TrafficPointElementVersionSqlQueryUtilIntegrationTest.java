package ch.sbb.exportservice.integration;

import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.entity.sepodi.TrafficPointElementVersion;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.reader.TrafficPointElementVersionRowMapper;
import ch.sbb.exportservice.reader.TrafficPointElementVersionSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TrafficPointElementVersionSqlQueryUtilIntegrationTest extends BaseSqlIntegrationTest {

  @Test
  void shouldReturnWorldFullWithServicePointAndBusinessOrganisation() throws SQLException {
    //given
    final int servicePointNumber = 1205887;
    insertServicePoint(servicePointNumber, LocalDate.of(2020, 1, 1), LocalDate.of(2099, 12, 31), Country.SWITZERLAND);
    insertTrafficPoint("ch:1:sloid:1", LocalDate.of(1950, 1, 1), LocalDate.of(1960, 1, 1));
    insertTrafficPoint("ch:1:sloid:2", LocalDate.of(2020, 1, 1), LocalDate.of(2030, 1, 1));
    insertTrafficPoint("ch:1:sloid:3", LocalDate.of(2050, 1, 1), LocalDate.of(2060, 1, 1));

    final String sqlQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_FULL);

    //when
    final List<TrafficPointElementVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(3);
    final TrafficPointElementVersion trafficPointElementVersion = result.stream().filter(t -> "ch:1:sloid:2".equals(t.getSloid()))
        .findFirst().orElseThrow();
    assertThat(trafficPointElementVersion).isNotNull();
    assertThat(trafficPointElementVersion.getServicePointBusinessOrganisationRelation().getBusinessOrganisation()).isEqualTo(
        "ch:1:sboid:101999");
  }

  @Test
  void shouldReturnTimetableFuture() throws SQLException {
    //given
    final LocalDate futureDate = FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now());
    final int servicePointNumber = 1205887;
    insertServicePoint(servicePointNumber, futureDate, futureDate, Country.SWITZERLAND);
    final String sloid = "ch:1:sloid:77559:0:2";
    insertTrafficPoint(sloid, futureDate, futureDate);
    insertTrafficPoint("ch:1:sloid:1", futureDate.minusMonths(5), futureDate.minusMonths(4));
    final String sqlQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_FUTURE_TIMETABLE);

    //when
    final List<TrafficPointElementVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    final TrafficPointElementVersion trafficPointElementVersion = result.stream().filter(t -> t.getSloid().equals(sloid))
        .findFirst().orElseThrow();
    assertThat(trafficPointElementVersion).isNotNull();
    assertThat(trafficPointElementVersion.getServicePointBusinessOrganisationRelation().getBusinessOrganisation()).isEqualTo(
        "ch:1:sboid:101999");
    result.forEach(t -> assertThat(isDateInRange(futureDate, t.getValidFrom(), t.getValidTo())).isTrue());
  }

  @Test
  void shouldReturnWorldActual() throws SQLException {
    //given
    final int servicePointNumber = 1205887;
    final LocalDate now = LocalDate.now();
    insertServicePoint(servicePointNumber, now, now, Country.SWITZERLAND);
    final String sloid = "ch:1:sloid:77559:0:2";
    insertTrafficPoint(sloid, now, now);
    insertTrafficPoint("ch:1:sloid:1", now.minusMonths(5), now.minusMonths(4));
    final String sqlQuery = TrafficPointElementVersionSqlQueryUtil.getSqlQuery(ExportTypeV2.WORLD_FUTURE_TIMETABLE);

    //when
    final List<TrafficPointElementVersion> result = executeQuery(sqlQuery);

    //then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    final TrafficPointElementVersion trafficPointElementVersion = result.stream().filter(t -> t.getSloid().equals(sloid))
        .findFirst().orElseThrow();
    assertThat(trafficPointElementVersion).isNotNull();
    assertThat(trafficPointElementVersion.getServicePointBusinessOrganisationRelation().getBusinessOrganisation()).isEqualTo(
        "ch:1:sboid:101999");
    result.forEach(t -> assertThat(isDateInRange(now, t.getValidFrom(), t.getValidTo())).isTrue());
  }

  private List<TrafficPointElementVersion> executeQuery(String sqlQuery) throws SQLException {
    List<TrafficPointElementVersion> result = new ArrayList<>();
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      assertThat(resultSet).isNotNull();
      TrafficPointElementVersionRowMapper mapper = new TrafficPointElementVersionRowMapper();
      while (resultSet.next()) {
        TrafficPointElementVersion trafficPointElementVersion = mapper.mapRow(resultSet, resultSet.getRow());
        result.add(trafficPointElementVersion);
      }
    }
    connection.close();
    return result;
  }

  private void insertTrafficPoint(String sloid, LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        insert into traffic_point_element_version (id, sloid, parent_sloid, designation,
        designation_operational, traffic_point_element_type, length, boarding_area_height, compass_direction,
        service_point_number, valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        values (nextval('traffic_point_element_version_seq'), '%s', null, null, '2', 'BOARDING_PLATFORM', 18.000, 2.00, null, 1205887,
        '%s', '%s', '2022-03-03 07:56:42.000000', 'fs45117', '2022-05-03 11:50:46.000000', 'e536178', 0);
        """
        .formatted(sloid, formatDate(validFrom), formatDate(validTo));
    final Connection connection = servicePointDataSource.getConnection();
    try (final PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

}
