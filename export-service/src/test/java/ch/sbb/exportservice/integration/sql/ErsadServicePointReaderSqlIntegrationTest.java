package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.job.prm.ersad.reader.ErsadServicePointReader;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class ErsadServicePointReaderSqlIntegrationTest extends BaseSqlIntegrationTest {

  private ErsadServicePointReader ersadServicePointReader;

  @BeforeEach
  void setUp() {
    ersadServicePointReader = new ErsadServicePointReader(new NamedParameterJdbcTemplate(servicePointDataSource));
  }

  @Test
  void shouldReadServicePointWithGeolocation() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertServicePointGeolocation(7001L, 2596624.4, 1222062.1);
    insertServicePointVersion(1001L, 8500100, "ch:1:sloid:8500100", "Tavannes", 7001L, today, today);

    // When
    Map<String, ServicePointVersion> servicePoints = ersadServicePointReader.readServicePoints(Set.of("ch:1:sloid:8500100"));

    // Then
    assertThat(servicePoints).hasSize(1);
    ServicePointVersion servicePointVersion = servicePoints.get("ch:1:sloid:8500100");
    assertThat(servicePointVersion.getDesignationOfficial()).isEqualTo("Tavannes");
    assertThat(servicePointVersion.getServicePointGeolocation()).isNotNull();
    assertThat(servicePointVersion.getServicePointGeolocation().getEast()).isEqualTo(2596624.4);
    assertThat(servicePointVersion.getServicePointGeolocation().getNorth()).isEqualTo(1222062.1);
  }

  @Test
  void shouldReadTrafficPointDesignationForPlatform() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertServicePointGeolocation(7101L, 2596600.0, 1222000.0);
    insertServicePointVersion(1101L, 8500101, "ch:1:sloid:8500101", "Tavannes", 7101L, today, today);
    insertTrafficPointElementVersion(1201L, "ch:1:sloid:8500101:0:3", 8500101, "3", today, today);

    // When
    Map<String, String> designations = ersadServicePointReader.readTrafficPointDesignations(Set.of("ch:1:sloid:8500101:0:3"));

    // Then
    assertThat(designations).containsEntry("ch:1:sloid:8500101:0:3", "3");
  }

  @Test
  void shouldChunkLargeSloidSets() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    Set<String> sloids = new HashSet<>();
    for (int i = 0; i < 1200; i++) {
      int number = 8600000 + i;
      String sloid = "ch:1:sloid:%d".formatted(number);
      sloids.add(sloid);
      long geolocationId = 9000L + i;
      insertServicePointGeolocation(geolocationId, 2600000.0 + i, 1200000.0 + i);
      insertServicePointVersion(2000L + i, number, sloid, "Station " + i, geolocationId, today, today);
    }

    // When
    Map<String, ServicePointVersion> servicePoints = ersadServicePointReader.readServicePoints(sloids);

    // Then
    assertThat(servicePoints).hasSize(1200);
  }

  private void insertServicePointVersion(long id, int number, String sloid, String designationOfficial, long geolocationId,
      LocalDate validFrom, LocalDate validTo) throws SQLException {
    String insert = """
        INSERT INTO service_point_version (id, service_point_geolocation_id, number, sloid, number_short, country, designation_long,
                                           designation_official, abbreviation, status_didok3, sort_code_of_destination_station,
                                           business_organisation, operating_point_type, stop_point_type, status,
                                           operating_point_kilometer_master, operating_point_route_network, valid_from, valid_to, creation_date,
                                           creator, edition_date, editor, version, freight_service_point, operating_point,
                                           operating_point_with_timetable, operating_point_technical_timetable_type,
                                           operating_point_traffic_point_type)
        VALUES (%d, %d, %d, '%s', %d, '%s', null, '%s', null, 'IN_OPERATION', null, 'ch:1:sboid:101999', null, 'UNKNOWN',
                'VALIDATED', null, false, '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000',
                'u123456', 0, false, true, true, null, null);
        """.formatted(id, geolocationId, number, sloid, number, Country.SWITZERLAND.name(), designationOfficial, formatDate(validFrom),
        formatDate(validTo));
    execute(insert);
  }

  private void insertServicePointGeolocation(long id, double east, double north) throws SQLException {
    String insert = """
        INSERT INTO service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                                       swiss_district_name, swiss_district_number, swiss_municipality_number,
                                                       swiss_municipality_name, swiss_locality_name, creation_date, creator, edition_date, editor, version)
        VALUES (%d, 'LV95', %.11f, %.11f, null, 'SWITZERLAND', null, null, null, null, null, null,
                '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0);
        """.formatted(id, east, north);
    execute(insert);
  }

  private void insertTrafficPointElementVersion(long id, String sloid, int servicePointNumber, String designation, LocalDate validFrom,
      LocalDate validTo) throws SQLException {
    String insert = """
        INSERT INTO traffic_point_element_version (id, sloid, parent_sloid, status, designation, designation_operational,
                                                   traffic_point_element_type, length, boarding_area_height, compass_direction,
                                                   service_point_number, valid_from, valid_to, traffic_point_geolocation_id,
                                                   creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', null, 'VALIDATED', '%s', null, 'BOARDING_PLATFORM', null, null, null, %d, '%s', '%s', null,
                '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0);
        """.formatted(id, sloid, designation, servicePointNumber, formatDate(validFrom), formatDate(validTo));
    execute(insert);
  }

  private void execute(String sql) throws SQLException {
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }
}
