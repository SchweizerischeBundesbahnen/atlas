package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
  void shouldFindActualServicePointWithGeolocation() {
    // given
    ServicePointVersion servicePointSwissWithGeoLocationVersion1 = ServicePointTestData.getBernWyleregg();
    servicePointSwissWithGeoLocationVersion1.setValidFrom(LocalDate.now().plusDays(1));
    servicePointSwissWithGeoLocationVersion1.setValidTo(LocalDate.now().plusDays(30));
    servicePointSwissWithGeoLocationVersion1.setDesignationOfficial("bern");
    servicePointVersionRepository.save(servicePointSwissWithGeoLocationVersion1);
    ServicePointVersion servicePointSwissWithGeoLocationVersion2 = ServicePointTestData.getBernWyleregg();
    servicePointSwissWithGeoLocationVersion2.setValidFrom(LocalDate.now().plusDays(31));
    servicePointSwissWithGeoLocationVersion2.setValidTo(LocalDate.now().plusDays(60));
    servicePointSwissWithGeoLocationVersion2.setDesignationOfficial("bern2");
    servicePointVersionRepository.save(servicePointSwissWithGeoLocationVersion2);

    ServicePointVersion servicePointSwissWithoutGeoLocation = ServicePointTestData.getBernWyleregg();
    servicePointSwissWithoutGeoLocation.setServicePointGeolocation(null);
    servicePointSwissWithoutGeoLocation.setDesignationOfficial("1");
    servicePointSwissWithoutGeoLocation.setSloid("ch:1:sloid:89001");
    servicePointSwissWithoutGeoLocation.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589001));
    servicePointSwissWithoutGeoLocation.setNumberShort(89001);
    servicePointSwissWithoutGeoLocation.setValidTo(LocalDate.now().plusDays(1));
    servicePointVersionRepository.save(servicePointSwissWithoutGeoLocation);

    ServicePointVersion servicePointSwissWithGeoLocationRevoked = ServicePointTestData.getBernWyleregg();
    servicePointSwissWithGeoLocationRevoked.setValidTo(LocalDate.now().plusDays(1));
    servicePointSwissWithGeoLocationRevoked.setStatus(Status.REVOKED);
    servicePointSwissWithGeoLocationRevoked.setDesignationOfficial("2");
    servicePointSwissWithGeoLocationRevoked.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589002));
    servicePointSwissWithGeoLocationRevoked.setSloid("ch:1:sloid:89002");
    servicePointSwissWithGeoLocationRevoked.setNumberShort(89002);
    servicePointVersionRepository.save(servicePointSwissWithGeoLocationRevoked);

    ServicePointVersion servicePointSwissWithGeoLocationInReview = ServicePointTestData.getBernWyleregg();
    servicePointSwissWithGeoLocationInReview.setValidTo(LocalDate.now().plusDays(1));
    servicePointSwissWithGeoLocationInReview.setStatus(Status.IN_REVIEW);
    servicePointSwissWithGeoLocationInReview.setSloid("ch:1:sloid:89003");
    servicePointSwissWithGeoLocationInReview.setNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8589003));
    servicePointSwissWithGeoLocationInReview.setDesignationOfficial("3");
    servicePointSwissWithGeoLocationInReview.setNumberShort(89003);
    servicePointVersionRepository.save(servicePointSwissWithGeoLocationInReview);

    // when
    List<ServicePointSwissWithGeoTransfer> result = servicePointVersionRepository.findActualServicePointWithGeolocation();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.getFirst().getId()).isEqualTo(servicePointSwissWithGeoLocationVersion1.getId());
    assertThat(result.getFirst().getSloid()).isEqualTo(servicePointSwissWithGeoLocationVersion1.getSloid());
    assertThat(result.getFirst().getValidFrom()).isEqualTo(servicePointSwissWithGeoLocationVersion1.getValidFrom());
    assertThat(result.getLast().getId()).isEqualTo(servicePointSwissWithGeoLocationVersion2.getId());
    assertThat(result.getLast().getSloid()).isEqualTo(servicePointSwissWithGeoLocationVersion2.getSloid());
    assertThat(result.getLast().getValidFrom()).isEqualTo(servicePointSwissWithGeoLocationVersion2.getValidFrom());

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
