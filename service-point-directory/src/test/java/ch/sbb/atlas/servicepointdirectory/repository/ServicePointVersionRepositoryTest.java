package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
 class ServicePointVersionRepositoryTest {

  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
   ServicePointVersionRepositoryTest(
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @AfterEach
  void tearDown() {
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldSaveServicePointVersionWithUicCountry() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
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
    assertThat(savedVersion.isStopPoint()).isFalse();
  }

  @Test
  void shouldSaveServicePointVersionWithGeolocation() {
    // given
    ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600037.945)
        .north(1199749.812)
        .height(2540.21)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .build();

    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
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
    assertThat(savedVersion.getServicePointGeolocation().getSwissCanton().getName()).isEqualTo("Bern");
  }

  @Test
  void shouldSaveServicePointVersionWithTwoCategories() {
    // given

    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
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
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNull();
    assertThat(savedVersion.getCategories()).isEmpty();

    assertThat(savedVersion.isOperatingPoint()).isTrue();
  }

  @Test
  void shouldSaveServicePointVersionWithComment() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();

  }

  @Test
  void shouldSaveStopPlace() {
    // given
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    // when
    ServicePointVersion savedVersion = servicePointVersionRepository.save(servicePoint);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getServicePointGeolocation()).isNull();
    assertThat(savedVersion.getCategories()).isEmpty();
    assertThat(savedVersion.isOperatingPoint()).isTrue();
  }

  @Test
  void shouldServicePointExistsByServicePointNumber() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8507000);
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .number(servicePointNumber)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    servicePointVersionRepository.save(servicePoint);

    // when
    boolean result = servicePointVersionRepository.existsByNumber(servicePointNumber);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void shouldServicePointNotExistsByServicePointNumber() {
    // given
    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8507001);
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .number(servicePointNumber)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .stopPointType(StopPointType.ORDERLY)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    servicePointVersionRepository.save(servicePoint);

    // when
    boolean result = servicePointVersionRepository.existsByNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));

    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldLoadServicePointVersionWithCategoriesAndMeansOfTransport() {
    // given
    ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600037.945)
        .north(1199749.812)
        .height(2540.21)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .build();

    ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8507000);
    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .number(servicePointNumber)
        .numberShort(1)
        .country(Country.SWITZERLAND)
        .designationLong("long designation")
        .designationOfficial("official designation")
        .abbreviation("BE")
        .businessOrganisation("somesboid")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .categories(Set.of(Category.BORDER_POINT, Category.DISTRIBUTION_POINT))
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN))
        .servicePointGeolocation(servicePointGeolocation)
        .build();
    servicePointGeolocation.setServicePointVersion(servicePoint);
    servicePointVersionRepository.save(servicePoint);

    // when
    List<ServicePointVersion> result = servicePointVersionRepository.findAllByNumberOrderByValidFrom(
        servicePointNumber);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCategories()).hasSize(2);
    assertThat(result.get(0).getMeansOfTransport()).hasSize(2);
    assertThat(result.get(0).hasGeolocation()).isTrue();
    assertThat(result.get(0).getServicePointGeolocation()).isNotNull();
  }

    @Test
    void shouldFindAllByNumberAndRouteNetworkTrue() {
        // given

        ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(8507000);
        ServicePointVersion servicePoint = ServicePointVersion
                .builder()
                .number(servicePointNumber)
                .numberShort(1)
                .country(Country.SWITZERLAND)
                .designationLong("long designation")
                .designationOfficial("official designation")
                .abbreviation("BE")
                .businessOrganisation("somesboid")
                .status(Status.VALIDATED)
                .validFrom(LocalDate.of(2020, 1, 1))
                .validTo(LocalDate.of(2020, 12, 31))
                .categories(Set.of(Category.BORDER_POINT, Category.DISTRIBUTION_POINT))
                .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN))
                .build();
        servicePointVersionRepository.save(servicePoint);

        ServicePointVersion servicePointVersion1 = ServicePointVersion
                .builder()
                .number(servicePointNumber)
                .numberShort(1)
                .country(Country.SWITZERLAND)
                .designationLong("long designation1")
                .designationOfficial("official designation1")
                .abbreviation("BE")
                .businessOrganisation("somesboid")
                .operatingPointRouteNetwork(true)
                .status(Status.VALIDATED)
                .validFrom(LocalDate.of(2021, 1, 1))
                .validTo(LocalDate.of(2021, 12, 31))
                .categories(Set.of(Category.POINT_OF_SALE))
                .meansOfTransport(Set.of(MeanOfTransport.TRAM))
                .build();
        servicePointVersionRepository.save(servicePointVersion1);

        ServicePointVersion servicePointVersion2 = ServicePointVersion
                .builder()
                .number(servicePointNumber)
                .numberShort(1)
                .country(Country.SWITZERLAND)
                .designationLong("long designation2")
                .designationOfficial("official designation2")
                .abbreviation("BE")
                .businessOrganisation("somesboid")
                .operatingPointRouteNetwork(true)
                .status(Status.VALIDATED)
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2022, 12, 31))
                .categories(Set.of(Category.POINT_OF_SALE))
                .meansOfTransport(Set.of(MeanOfTransport.BOAT))
                .build();
        servicePointVersionRepository.save(servicePointVersion2);

        // when
        List<ServicePointVersion> result = servicePointVersionRepository
                .findAllByNumberAndOperatingPointRouteNetworkTrueOrderByValidFrom(servicePointNumber);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDesignationLong()).isEqualTo("long designation1");
        assertThat(result.get(1).getDesignationLong()).isEqualTo("long designation2");
    }

}
