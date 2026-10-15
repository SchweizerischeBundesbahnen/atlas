package ch.sbb.exportservice.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.job.prm.ersad.reader.ErsadPrmReader;
import ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class ErsadPrmReaderSqlIntegrationTest extends BasePrmSqlIntegrationTest {

  private ErsadPrmReader ersadPrmReader;

  @BeforeEach
  void setUp() {
    ersadPrmReader = new ErsadPrmReader(new NamedParameterJdbcTemplate(prmDataSource));
  }

  @Test
  void shouldReadOnlyValidatedTrainStopPointsValidToday() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertStopPointVersion(101L, "ch:1:sloid:8500100", 8500100, "VALIDATED", today, today, Set.of(MeanOfTransport.TRAIN));
    insertStopPointVersion(102L, "ch:1:sloid:8500101", 8500101, "DRAFT", today, today, Set.of(MeanOfTransport.TRAIN));
    insertStopPointVersion(103L, "ch:1:sloid:8500102", 8500102, "VALIDATED", today, today, Set.of(MeanOfTransport.BUS));
    insertStopPointVersion(104L, "ch:1:sloid:8500103", 8500103, "VALIDATED", today.minusDays(10), today.minusDays(1),
        Set.of(MeanOfTransport.TRAIN));

    // When
    Map<String, StopPointVersion> stopPoints = ersadPrmReader.readValidatedTrainStopPoints();

    // Then
    assertThat(stopPoints)
        .hasSize(1)
        .containsOnlyKeys("ch:1:sloid:8500100");
  }

  @Test
  void shouldExcludeStopPointsWithoutTrain() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertStopPointVersion(201L, "ch:1:sloid:8500200", 8500200, "VALIDATED", today, today,
        Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM));

    // When
    Map<String, StopPointVersion> stopPoints = ersadPrmReader.readValidatedTrainStopPoints();

    // Then
    assertThat(stopPoints).isEmpty();
  }

  @Test
  void shouldExcludeDraftStopPoints() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertStopPointVersion(301L, "ch:1:sloid:8500300", 8500300, "DRAFT", today, today, Set.of(MeanOfTransport.TRAIN));

    // When
    Map<String, StopPointVersion> stopPoints = ersadPrmReader.readValidatedTrainStopPoints();

    // Then
    assertThat(stopPoints).isEmpty();
  }

  @Test
  void shouldGroupPlatformsByParentSloid() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertPlatformVersion(401L, "ch:1:sloid:8500400:0:1", "ch:1:sloid:8500400", 8500400, today, today);
    insertPlatformVersion(402L, "ch:1:sloid:8500400:0:2", "ch:1:sloid:8500400", 8500400, today, today);
    insertPlatformVersion(403L, "ch:1:sloid:8500500:0:1", "ch:1:sloid:8500500", 8500500, today, today);

    // When
    Map<String, List<PlatformVersion>> platforms = ersadPrmReader.readPlatforms(Set.of("ch:1:sloid:8500400", "ch:1:sloid:8500500"));

    // Then
    assertThat(platforms).hasSize(2);
    assertThat(platforms.get("ch:1:sloid:8500400")).hasSize(2);
    assertThat(platforms.get("ch:1:sloid:8500400").stream().map(PlatformVersion::getSloid).collect(Collectors.toSet()))
        .containsExactlyInAnyOrder("ch:1:sloid:8500400:0:1", "ch:1:sloid:8500400:0:2");
    assertThat(platforms.get("ch:1:sloid:8500500")).hasSize(1);
  }

  @Test
  void shouldReadParkingLotsOfStation() throws SQLException {
    // Given
    LocalDate today = LocalDate.now();
    insertParkingLotVersion(501L, "ch:1:sloid:8500600:parking:1", "ch:1:sloid:8500600", 8500600, today, today);
    insertParkingLotVersion(502L, "ch:1:sloid:8500700:parking:1", "ch:1:sloid:8500700", 8500700, today, today);

    // When
    Map<String, List<ParkingLotVersion>> parkingLots = ersadPrmReader.readParkingLots(Set.of("ch:1:sloid:8500600"));

    // Then
    assertThat(parkingLots).hasSize(1);
    assertThat(parkingLots.get("ch:1:sloid:8500600")).singleElement()
        .extracting(ParkingLotVersion::getSloid)
        .isEqualTo("ch:1:sloid:8500600:parking:1");
  }

  private void insertStopPointVersion(long id, String sloid, int number, String status, LocalDate validFrom, LocalDate validTo,
      Set<MeanOfTransport> meansOfTransport) throws SQLException {
    String insert = """
        INSERT INTO stop_point_version (id, sloid, number, free_text, address, zip_code, city, alternative_transport,
                                        shuttle_service, alternative_transport_condition, assistance_availability, assistance_condition,
                                        assistance_service, audio_ticket_machine, additional_information, dynamic_audio_system,
                                        dynamic_optic_system, info_ticket_machine, interoperable, url, visual_info, wheelchair_ticket_machine,
                                        assistance_request_fulfilled, ticket_machine, valid_from, valid_to, creation_date, creator, edition_date,
                                        editor, version, status)
        VALUES (%d, '%s', %d, null, 'Rue de la Gare', '2710', 'Tavannes', 'NO',
                'NO', null, 'YES', null, 'YES', 'YES', null, 'YES', 'YES', null, true, 'https://www.sbb.ch',
                'YES', 'YES', 'YES', 'YES', '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
                '2022-02-19 09:54:38.000000', 'u123456', 0, '%s');
        """.formatted(id, sloid, number, formatDate(validFrom), formatDate(validTo), status);
    execute(insert);
    for (MeanOfTransport meanOfTransport : meansOfTransport) {
      execute("INSERT INTO stop_point_version_means_of_transport (stop_point_version_id, means_of_transport) VALUES (%d, '%s');"
          .formatted(id, meanOfTransport.name()));
    }
  }

  private void insertPlatformVersion(long id, String sloid, String parentSloid, int number, LocalDate validFrom, LocalDate validTo)
      throws SQLException {
    String insert = """
        INSERT INTO platform_version (id, sloid, number, parent_service_point_sloid, shuttle, boarding_device, additional_information,
                                      advice_access_info, contrasting_areas, dynamic_audio, dynamic_visual, height, inclination,
                                      inclination_longitudinal, inclination_width, level_access_wheelchair, partial_elevation,
                                      superelevation, tactile_system, attention_field, vehicle_access, wheelchair_area_length,
                                      wheelchair_area_width, valid_from, valid_to, creation_date, creator, edition_date, editor, version, status)
        VALUES (%d, '%s', %d, '%s', 'YES', 'LIFTS', null, 'access info', 'YES', 'YES', 'YES', 0, 0, 0, 0, 'YES',
                false, 0, 'YES', 'YES', 'TO_BE_COMPLETED', 0, 0, '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
                '2022-02-19 09:54:38.000000', 'u123456', 0, 'VALIDATED');
        """.formatted(id, sloid, number, parentSloid, formatDate(validFrom), formatDate(validTo));
    execute(insert);
  }

  private void insertParkingLotVersion(long id, String sloid, String parentSloid, int number, LocalDate validFrom, LocalDate validTo)
      throws SQLException {
    String insert = """
        INSERT INTO parking_lot_version (id, sloid, number, parent_service_point_sloid, designation, additional_information,
                                         places_available, prm_places_available, valid_from, valid_to, creation_date, creator, edition_date,
                                         editor, version, status)
        VALUES (%d, '%s', %d, '%s', 'Parkplatz Bahnhof', null, 'YES', 'YES', '%s', '%s', '2022-02-19 09:54:38.000000',
                'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0, 'VALIDATED');
        """.formatted(id, sloid, number, parentSloid, formatDate(validFrom), formatDate(validTo));
    execute(insert);
  }

  private void execute(String sql) throws SQLException {
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }
}
