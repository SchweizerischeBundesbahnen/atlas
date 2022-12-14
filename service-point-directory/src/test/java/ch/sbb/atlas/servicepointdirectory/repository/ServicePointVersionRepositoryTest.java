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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public class ServicePointVersionRepositoryTest {

  private final ServicePointVersionRepository servicePointVersionRepository;
  private final ServicePointCommentRepository servicePointCommentRepository;

  @Autowired
  public ServicePointVersionRepositoryTest(ServicePointVersionRepository servicePointVersionRepository,
      ServicePointCommentRepository servicePointCommentRepository) {
    this.servicePointVersionRepository = servicePointVersionRepository;
    this.servicePointCommentRepository = servicePointCommentRepository;
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

    List<ServicePointVersion> servicePointVersions = new ArrayList<>();
    for (ServicePointCsvModel csvModel : servicePointCsvModels) {
      // GeoLocation

      // ServicePoint
      ServicePointVersion servicePoint = ServicePointVersion.builder()
          .number(csvModel.getDIDOK_CODE())
          .checkDigit(csvModel.getDIDOK_CODE() % 10)
          .numberShort(csvModel.getNUMMER())
          .country(Country.from(csvModel.getLAENDERCODE()))
          .designationLong(csvModel.getBEZEICHNUNG_LANG())
          .designationOfficial(csvModel.getBEZEICHNUNG_OFFIZIELL())
          .abbreviation(csvModel.getABKUERZUNG())
          .statusDidok3(ServicePointStatus.from(csvModel.getSTATUS()))
          .businessOrganisation(csvModel.getGO_NUMMER().toString()) // TODO: map to sboid GO_Export.csv
          .hasGeolocation(!csvModel.getIS_VIRTUELL())
          .status(Status.VALIDATED)
          .validFrom(LocalDate.parse(csvModel.getGUELTIG_VON()))
          .validTo(LocalDate.parse(csvModel.getGUELTIG_BIS()))
          .categories(
              Arrays.stream(Objects.nonNull(csvModel.getDS_KATEGORIEN_IDS()) ? csvModel.getDS_KATEGORIEN_IDS().split("\\|") :
                      new String[]{})
                  .map(categoryIdStr -> Category.from(Integer.parseInt(categoryIdStr)))
                  .filter(Objects::nonNull).collect(Collectors.toSet())
          )
          .operatingPointType(OperatingPointType.from(csvModel.getBPVB_BETRIEBSPUNKT_ART_ID()))
          //.servicePointGeolocation() // TODO: map to servicePointGeolocation entity and save before servicePoint
          .creationDate(LocalDateTime.parse(csvModel.getERSTELLT_AM(),
              new DateTimeFormatterBuilder()
                  .parseCaseInsensitive()
                  .append(ISO_LOCAL_DATE)
                  .appendLiteral(' ')
                  .append(ISO_LOCAL_TIME)
                  .toFormatter()
          ))
          .creator(csvModel.getERSTELLT_VON())
          .editionDate(LocalDateTime.parse(csvModel.getGEAENDERT_AM(),
              new DateTimeFormatterBuilder()
                  .parseCaseInsensitive()
                  .append(ISO_LOCAL_DATE)
                  .appendLiteral(' ')
                  .append(ISO_LOCAL_TIME)
                  .toFormatter()
          ))
          .editor(csvModel.getGEAENDERT_VON())
          .build();
      servicePointVersions.add(servicePoint);
    }

    List<ServicePointVersion> servicePointVersionsSaved = servicePointVersionRepository.saveAll(servicePointVersions);

    assertThat(servicePointVersionsSaved).hasSize(10);
    for (ServicePointVersion savedVersion : servicePointVersionsSaved) {
      assertThat(savedVersion.getId()).isNotNull();
    }
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
        .meansOfTransport(Set.of(MeanOfTransport.COACH))
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
