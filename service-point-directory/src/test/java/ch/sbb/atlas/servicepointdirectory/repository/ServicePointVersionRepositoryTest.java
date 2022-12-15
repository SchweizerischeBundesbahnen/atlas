package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.*;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointCsvModel;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointImportService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
//@Transactional
public class ServicePointVersionRepositoryTest {

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final ServicePointCommentRepository servicePointCommentRepository;
  private final ServicePointImportService servicePointImportService;

  @Autowired
  public ServicePointVersionRepositoryTest(ServicePointVersionRepository servicePointVersionRepository,
      ServicePointCommentRepository servicePointCommentRepository,
      ServicePointImportService servicePointImportService) {
    this.servicePointVersionRepository = servicePointVersionRepository;
    this.servicePointCommentRepository = servicePointCommentRepository;
    this.servicePointImportService = servicePointImportService;
  }

  @AfterEach
  void tearDown() {
    servicePointVersionRepository.deleteAll();
    servicePointCommentRepository.deleteAll();
  }

  @Test
  void shouldSaveServicePointVersionWithUicCountry() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(1)
        .checkDigit(1)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .hasGeolocation(true)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNull();
    assertThat(savedVersion.getCategories()).isEmpty();
    assertThat(savedVersion.isOperatingPoint()).isFalse();
    assertThat(savedVersion.isStopPlace()).isFalse();
  }

  @Test
  void shouldSaveServicePointVersionWithGeolocation() {
    // given
    ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation.builder()
        .source_spatial_ref(1)
        .lv03east(600037.945)
        .lv03north(199749.812)
        .lv95east(2600037.945)
        .lv95north(1199749.812)
        .wgs84east(7.43913089)
        .wgs84north(46.94883229)
        .height(540.2)
        .isoCountryCode(Country.SWITZERLAND.getIsoCode())
        .swissCantonFsoNumber(5)
        .swissCantonName("Bern")
        .swissCantonNumber(5)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .build();

    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(1)
        .checkDigit(1)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .hasGeolocation(true)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .servicePointGeolocation(servicePointGeolocation)
        .build();

    servicePointGeolocation.setServicePointVersion(servicePoint);

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation().getSwissCantonName()).isEqualTo("Bern");
  }

  @Test
  void shouldSaveServicePointVersionWithTwoCategories() {
    // given

    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(1)
        .checkDigit(1)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .hasGeolocation(true)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .categories(Set.of(Category.BORDER_POINT, Category.DISTRIBUTION_POINT))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNull();
    assertThat(savedVersion.getCategories()).hasSize(2);
  }

  @Test
  void shouldSaveServicePointVersionWithOperatingPointType() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(1)
        .checkDigit(1)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .hasGeolocation(true)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .operatingPointType(OperatingPointType.STOP_POINT)
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNull();
    assertThat(savedVersion.getCategories()).isEmpty();

    assertThat(savedVersion.isOperatingPoint()).isTrue();
    assertThat(savedVersion.getOperatingPointType().hasTimetable()).isTrue();
    assertThat(savedVersion.getOperatingPointType().getDesignationDe()).isEqualTo("Haltestelle");
  }

  // ----------------------------------
  // Dienststellen All V3 Csv Import Tests
  // ----------------------------------
  @Test
  void parseFirst10LinesFromDienststellenAllV3CsvAndSaveToDB() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DienststellenV3.csv");
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

    assertThat(servicePointCsvModels).hasSize(10);

    servicePointImportService.importSPCsvModel(servicePointCsvModels);

    assertThat(servicePointVersionRepository.findAll()).hasSize(10);
  }

  @Test
  void parseFirst10LinesFromDienststellenAllV3CsvAndSaveToDBFromGeolocationRepo() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DienststellenV3.csv");
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

    assertThat(servicePointCsvModels).hasSize(10);

    servicePointImportService.importSPCsvModel(servicePointCsvModels);

    assertThat(servicePointVersionRepository.findAll()).hasSize(10);
  }
  // ----------------------------------
  // Dienststellen All V3 Csv Import Tests End
  // ----------------------------------

  @Test
  void shouldSaveServicePointVersionWithComment() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(1)
        .checkDigit(1)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .hasGeolocation(true)
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    ServicePointComment savedComment = servicePointCommentRepository.save(ServicePointComment.builder()
        .servicePointNumber(savedVersion.getNumber())
        .comment("Pupazzi di neve")
        .build());

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedComment.getId()).isNotNull();
  }

  @Test
  void shouldSaveStopPlace() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion.builder()
        .number(1)
        .checkDigit(1)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .statusDidok3(ServicePointStatus.from(1))
        .businessOrganisation("somesboid")
        .hasGeolocation(true)
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPlaceType(StopPlaceType.ORDERLY)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNull();
    assertThat(savedVersion.getCategories()).isEmpty();
    assertThat(savedVersion.isOperatingPoint()).isFalse();
  }
}
