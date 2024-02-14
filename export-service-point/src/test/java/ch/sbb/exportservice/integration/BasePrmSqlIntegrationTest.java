package ch.sbb.exportservice.integration;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.BatchDataSourceConfigTest;
import ch.sbb.exportservice.PrmDbSchemaCreation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@PrmDbSchemaCreation
@BatchDataSourceConfigTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
abstract class BasePrmSqlIntegrationTest {

  @Autowired
  @Qualifier("prmDataSource")
  protected DataSource prmDataSource;

  protected void insertStopPoint(Integer number, String sloid, LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO stop_point_version (id, sloid, number, free_text, address, zip_code, city, alternative_transport,
                                              alternative_transport_condition, assistance_availability, assistance_condition,
                                              assistance_service, audio_ticket_machine, additional_information,
                                              dynamic_audio_system, dynamic_optic_system, info_ticket_machine, interoperable, url,
                                              visual_info, wheelchair_ticket_machine, assistance_request_fulfilled,
                                              ticket_machine, valid_from, valid_to, creation_date, creator, edition_date, editor,
                                              version)
        VALUES (1000, '%s', %d, null, 'Diessenhoferstrasse 21', '8245', 'Feuerthalen', 'NO', null, 'NOT_APPLICABLE',
        null, 'NO', 'NO', null, 'YES', 'NO', 'Hilfestellung für Sehbehinderte unter Telefon 0800 11 44 77', true, 'sbb.ch', 'YES',
        'YES', 'NO', 'YES', '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
        '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(sloid, number, formatDate(validFrom), formatDate(validTo));
    execute(insertSql);
    insertMeansOfTransport();
  }

  protected void insertMeansOfTransport() throws SQLException {
    final String insertSql = """
        INSERT INTO stop_point_version_means_of_transport (stop_point_version_id, means_of_transport) VALUES (1000, 'BUS');
        """;
    execute(insertSql);
  }

  protected void insertPlatform(int id, String sloid, ServicePointNumber parentServicePointNumber,
      LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO platform_version (id, sloid, number, parent_service_point_sloid, boarding_device, additional_information,
        advice_access_info, contrasting_areas, dynamic_audio, dynamic_visual, height, inclination, inclination_longitudinal, inclination_width, 
        level_access_wheelchair, partial_elevation, superelevation, tactile_system, vehicle_access, wheelchair_area_length, wheelchair_area_width, 
        valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', %d, '%s', 'LIFTS', '[Shuttle]', 
        'Somit ist ein Niveaugleicher Einstieg gesichert.', 'YES', 'YES', 'YES', 2.000, 2.000, 2.000, 0.000,
        'YES', false, 0.000, 'YES', 'TO_BE_COMPLETED', 0.000, 0.000,
         '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
        '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(id, sloid, parentServicePointNumber.getNumber(), ServicePointNumber.calculateSloid(parentServicePointNumber),
            formatDate(validFrom),
            formatDate(validTo));
    execute(insertSql);
  }

  protected void insertReferencePoint(int id, String sloid, ServicePointNumber parentServicePointNumber,
      LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO reference_point_version (id, sloid, parent_service_point_sloid, number, designation, additional_information,
        main_reference_point, reference_point_type, valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', '%s', %d, 'Haupteingang', 'Kann voll genutzt werden zum rein und raus gehen', false, 'MAIN_STATION_ENTRANCE',
         '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
        '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(id, sloid, ServicePointNumber.calculateSloid(parentServicePointNumber), parentServicePointNumber.getNumber(),
            formatDate(validFrom),
            formatDate(validTo));
    execute(insertSql);
  }

  protected void insertContactPoint(int id, String sloid, ServicePointNumber parentServicePointNumber,
      LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO contact_point_version (id, sloid, number, parent_service_point_sloid, designation, additional_information,
        induction_loop, opening_hours, wheelchair_access, type, valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', %d, '%s', 'Haupteingang', 'Kann voll genutzt werden zum rein und raus gehen', 'TO_BE_COMPLETED', 'Während der Fahrplanzeiten der Linie 2830',
        'TO_BE_COMPLETED', 'INFORMATION_DESK', '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
        '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(id, sloid, parentServicePointNumber.getNumber(), ServicePointNumber.calculateSloid(parentServicePointNumber),
            formatDate(validFrom),
            formatDate(validTo));
    execute(insertSql);
  }

  protected void insertToilet(int id, String sloid, ServicePointNumber parentServicePointNumber,
      LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO toilet_version (id, sloid, number, parent_service_point_sloid, designation, additional_information, wheelchair_toilet,
                            valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', %d, '%s', 'Haupteingang', 'Kann voll genutzt werden zum rein und raus gehen', 'TO_BE_COMPLETED', '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
        """
        .formatted(id, sloid, parentServicePointNumber.getNumber(), ServicePointNumber.calculateSloid(parentServicePointNumber),
            formatDate(validFrom),
            formatDate(validTo));
    execute(insertSql);
  }

  protected void insertParkingLot(int id, String sloid, ServicePointNumber parentServicePointNumber,
      LocalDate validFrom, LocalDate validTo) throws SQLException {
    final String insertSql = """
        INSERT INTO parking_lot_version (id, sloid, number, parent_service_point_sloid, designation, additional_information,
        places_available, prm_places_available, valid_from, valid_to, creation_date, creator, edition_date, editor, version)
        VALUES (%d, '%s', %d, '%s', 'Hauptparkplatz', 'Viel Platz', 'YES', 'NO', '%s', '%s', '2022-02-19 09:54:38.000000', 'u123456',
        '2022-02-19 09:54:38.000000', 'u123456', 0);
        """
        .formatted(id, sloid, parentServicePointNumber.getNumber(), ServicePointNumber.calculateSloid(parentServicePointNumber),
            formatDate(validFrom),
            formatDate(validTo));
    execute(insertSql);
  }

  private void execute(String insertSql) throws SQLException {
    final Connection connection = prmDataSource.getConnection();
    try (final PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

  protected String formatDate(LocalDate localDate) {
    return localDate.format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
  }
}
