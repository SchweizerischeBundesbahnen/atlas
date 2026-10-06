package ch.sbb.exportservice.integration;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_ERSAD_NETEX_XML_JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.s3.service.AmazonService;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.integration.sql.BasePrmSqlIntegrationTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ExportErsadNetexIntegrationTest extends BasePrmSqlIntegrationTest {

  private static final LocalDate FIXED_TEST_DATE = LocalDate.of(2026, 6, 8);

  @Autowired
  private JobOperator jobOperator;

  @Autowired
  @Qualifier(EXPORT_ERSAD_NETEX_XML_JOB_NAME)
  private Job exportErsadNetexXmlJob;

  @Autowired
  @Qualifier("servicePointDataSource")
  private DataSource servicePointDataSource;

  @MockitoBean
  private AmazonService amazonService;

  @MockitoBean
  @Qualifier("deleteErsadNetexXmlFileTasklet")
  private FileDeletingTaskletV2 deleteErsadNetexXmlFileTasklet;

  @MockitoBean
  private Clock clock;

  @BeforeEach
  void setUp() {
    ZoneId zoneId = ZoneId.systemDefault();
    when(clock.instant()).thenReturn(Instant.from(FIXED_TEST_DATE.atStartOfDay(zoneId).toInstant()));
    when(clock.getZone()).thenReturn(zoneId);
  }

  @Test
  void shouldExportTavannesAsNetex() throws Exception {
    // Given
    LocalDate today = LocalDate.now();
    insertServicePointGeolocation(9001L, 2582049.83754, 1229906.62316);
    insertServicePointVersion(9101L, 8500100, "ch:1:sloid:8500100", "Tavannes", 9001L, today, today);
    insertStopPointVersion(9201L, "ch:1:sloid:8500100", 8500100, today, today, MeanOfTransport.TRAIN);
    insertPlatformVersion(9301L, "ch:1:sloid:8500100:0:3", "ch:1:sloid:8500100", 8500100, today, today);
    insertToiletVersion(9401L, "ch:1:sloid:8500100:617340", "ch:1:sloid:8500100", 8500100, today, today);
    String capturedContent = runJobAndCaptureUploadedFileContent();

    // When / Then
    assertThat(capturedContent).contains("Tavannes")
        .contains("<StopPlace id=\"ch:1:sloid:8500100\"")
        .contains("<Quay id=\"ch:1:sloid:8500100:0:3\"")
        .contains("SanitaryEquipment");
  }

  @Test
  void shouldExcludeNonTrainStationsFromExport() throws Exception {
    // Given
    LocalDate today = LocalDate.now();
    insertServicePointGeolocation(9002L, 2600000.0, 1200000.0);
    insertServicePointVersion(9102L, 8599999, "ch:1:sloid:8599999", "Bus Only Stop", 9002L, today, today);
    insertStopPointVersion(9202L, "ch:1:sloid:8599999", 8599999, today, today, MeanOfTransport.BUS);
    String capturedContent = runJobAndCaptureUploadedFileContent();

    // When / Then
    assertThat(capturedContent).doesNotContain("ch:1:sloid:8599999");
  }

  @Test
  void shouldCompleteJobWhenNoStationsInScope() throws Exception {
    // Given (no stop points seeded)

    // When
    String capturedContent = runJobAndCaptureUploadedFileContent();

    // Then
    assertThat(capturedContent).contains("PublicationDelivery").contains("SiteFrame").doesNotContain("StopPlace");
  }

  private String runJobAndCaptureUploadedFileContent() throws Exception {
    var fileArgumentCaptor = org.mockito.ArgumentCaptor.forClass(File.class);
    when(amazonService.putZipFileCleanupBoth(any(), fileArgumentCaptor.capture(), any())).thenReturn(
        URI.create("https://sbb.ch").toURL());
    when(deleteErsadNetexXmlFileTasklet.execute(any(), any())).thenReturn(null);

    JobParameters jobParameters = BaseExportJobService.buildJobParameters(new JobParams(ExportTypeV2.ACTUAL));
    JobExecution jobExecution = jobOperator.start(exportErsadNetexXmlJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    assertThat(actualJobInstance.getJobName()).isEqualTo(EXPORT_ERSAD_NETEX_XML_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    File exportedXmlFile = fileArgumentCaptor.getValue();
    String content = Files.readString(exportedXmlFile.toPath());
    Files.delete(exportedXmlFile.toPath());
    return content;
  }

  private void insertStopPointVersion(long id, String sloid, int number, LocalDate validFrom, LocalDate validTo,
      MeanOfTransport meanOfTransport) throws SQLException {
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
                '2022-02-19 09:54:38.000000', 'u123456', 0, 'VALIDATED');
        """.formatted(id, sloid, number, formatDate(validFrom), formatDate(validTo));
    execute(insert);
    execute("INSERT INTO stop_point_version_means_of_transport (stop_point_version_id, means_of_transport) VALUES (%d, '%s');"
        .formatted(id, meanOfTransport.name()));
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

  private void insertToiletVersion(long id, String sloid, String parentSloid, int number, LocalDate validFrom, LocalDate validTo)
      throws SQLException {
    String insert = """
        INSERT INTO toilet_version (id, sloid, number, parent_service_point_sloid, designation, additional_information, wheelchair_toilet,
                            valid_from, valid_to, creation_date, creator, edition_date, editor, version, status)
        VALUES (%d, '%s', %d, '%s', 'Haupteingang', 'Kann voll genutzt werden zum rein und raus gehen', 'TO_BE_COMPLETED', '%s', '%s',
        '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0, 'VALIDATED');
        """.formatted(id, sloid, number, parentSloid, formatDate(validFrom), formatDate(validTo));
    execute(insert);
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
    executeOnServicePointDataSource(insert);
  }

  private void insertServicePointGeolocation(long id, double east, double north) throws SQLException {
    String insert = """
        INSERT INTO service_point_version_geolocation (id, spatial_reference, east, north, height, country, swiss_canton,
                                                       swiss_district_name, swiss_district_number, swiss_municipality_number,
                                                       swiss_municipality_name, swiss_locality_name, creation_date, creator, edition_date, editor, version)
        VALUES (%d, 'LV95', %.11f, %.11f, null, 'SWITZERLAND', null, null, null, null, null, null,
                '2022-02-19 09:54:38.000000', 'u123456', '2022-02-19 09:54:38.000000', 'u123456', 0);
        """.formatted(id, east, north);
    executeOnServicePointDataSource(insert);
  }

  private void executeOnServicePointDataSource(String sql) throws SQLException {
    Connection connection = servicePointDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

  private void execute(String sql) throws SQLException {
    Connection connection = prmDataSource.getConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.executeUpdate();
    }
    connection.close();
  }

}
