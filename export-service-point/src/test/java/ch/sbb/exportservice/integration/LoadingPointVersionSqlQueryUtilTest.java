package ch.sbb.exportservice.integration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.entity.LoadingPointVersion;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.reader.LoadingPointVersionRowMapper;
import ch.sbb.exportservice.reader.LoadingPointVersionSqlQueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LoadingPointVersionSqlQueryUtilTest extends BaseSqlTest {

  @Test
  void shouldReturnFullWorld() throws SQLException {
    // given
    final LocalDate now = LocalDate.now();
    final int servicePointNumber = 85091111;
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
  void shouldReturnActualDate() {

  }

  @Test
  void shouldReturnFutureTimetableDate() {

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
