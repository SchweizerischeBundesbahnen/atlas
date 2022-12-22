package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointComment;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPlaceType;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
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
         .locationTypes(LocationTypes
            .builder()
            .spatialReference(SpatialReference.LV95)
            .lv03east(600037.945)
            .lv03north(199749.812)
            .lv95east(2600037.945)
            .lv95north(1199749.812)
            .wgs84east(7.439130891)
            .wgs84north(46.948832291)
            .wgs84webEast(691419.90336)
            .wgs84webNorth(5811120.06939)
            .height(2540.21)
            .build())
        .country(Country.SWITZERLAND)
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
