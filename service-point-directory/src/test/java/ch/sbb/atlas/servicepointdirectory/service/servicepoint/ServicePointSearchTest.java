package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.ServicePointSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@IntegrationTest
public class ServicePointSearchTest {

  private final ServicePointService servicePointService;
  private final ServicePointVersionRepository servicePointVersionRepository;

  private ServicePointVersion servicePointVersion;

  @Autowired
  public ServicePointSearchTest(ServicePointService servicePointService,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.servicePointService = servicePointService;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void cleanUpDb() {
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldFindBernWylereggBySloid() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .sloids(List.of(servicePointVersion.getSloid()))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindBernWylereggBySloid() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .sloids(List.of("supersloid"))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByNumber() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .numbers(List.of(8589008))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
    assertThat(servicePointVersions.getContent().get(0).getNumber().getNumber()).isEqualTo(8589008);
  }

  @Test
  void shouldFindBernWylereggByNumberShort() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .numbersShort(List.of(89008))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
    assertThat(servicePointVersions.getContent().get(0).getNumberShort()).isEqualTo(89008);
  }

  @Test
  void shouldFindBernWylereggByCountry() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .countries(List.of(Country.SWITZERLAND))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldFindBernWylereggByStatus() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .statusRestrictions(List.of(Status.VALIDATED))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindBernWylereggByStatus() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .statusRestrictions(List.of(Status.DRAFT))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByMeansOfTransport() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .meansOfTransport(List.of(MeanOfTransport.BUS, MeanOfTransport.METRO))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindBernWylereggByMeansOfTransport() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .meansOfTransport(List.of(MeanOfTransport.TRAIN))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByOperatingPointBoolean() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .operatingPoint(true)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindBernWylereggByOperatingPointBoolean() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .operatingPoint(false)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByWithTimetableBoolean() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .withTimetable(true)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
    assertThat(servicePointVersions.getContent().get(0).isOperatingPointWithTimetable()).isTrue();
  }

  @Test
  void shouldNotFindBernWylereggByWithTimetableBoolean() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .withTimetable(false)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByWithValidOn() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .validOn(LocalDate.of(2021, 1, 1))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
    assertThat(servicePointVersions.getContent().get(0).isOperatingPointWithTimetable()).isTrue();
  }

  @Test
  void shouldNotFindBernWylereggByWithValidOn() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .validOn(LocalDate.of(2099, 1, 1))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByWithFromDate() {
    LocalDate fromDate = servicePointVersion.getValidFrom().minusDays(1);
    assertThat(fromDate).isBefore(servicePointVersion.getValidFrom());

    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .fromDate(fromDate)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindBernWylereggByWithFromDate() {
    LocalDate fromDate = servicePointVersion.getValidFrom().plusDays(1);
    assertThat(fromDate).isAfter(servicePointVersion.getValidFrom());

    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .fromDate(fromDate)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByWithToDate() {
    LocalDate toDate = servicePointVersion.getValidTo().plusDays(1);
    assertThat(toDate).isAfter(servicePointVersion.getValidTo());

    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .toDate(toDate)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindBernWylereggByWithToDate() {
    LocalDate toDate = servicePointVersion.getValidTo().minusDays(1);
    assertThat(toDate).isBefore(servicePointVersion.getValidTo());

    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .toDate(toDate)
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByWithCreatedAfter() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .createdAfter(servicePointVersion.getCreationDate().minusSeconds(1))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindBernWylereggByWithCreatedAfter() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .createdAfter(servicePointVersion.getCreationDate().plusSeconds(1))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByWithModifiedAfter() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .modifiedAfter(servicePointVersion.getEditionDate().minusSeconds(1))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindBernWylereggByWithModifiedAfter() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .modifiedAfter(servicePointVersion.getEditionDate().plusSeconds(1))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindServicePointsWithPageable() {
    // Given
    servicePointVersionRepository.deleteAll();

    int numberOfTotalServicePoints = 10;
    for (int i = 0; i < numberOfTotalServicePoints; i++) {
      servicePointVersionRepository.save(ServicePointTestData.getVersionWithCategoriesAndMeansOfTransport(i));
    }

    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.ofSize(5))
            .servicePointRequestParams(ServicePointRequestParams.builder().build()).build());

    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(numberOfTotalServicePoints);
  }

  @Test
  void shouldFindOperatingPointTechnicalTimetableTypeCountryBorder() {
    // Given
    servicePointVersionRepository.save(ServicePointTestData.createServicePointVersionWithCountryBorder());
    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .operatingPointTechnicalTimetableTypes(List.of(OperatingPointTechnicalTimetableType.COUNTRY_BORDER))
                .build()).build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Flüh Grenze");
  }

  @Test
  void shouldNotFindOperatingPointTechnicalTimetableTypePropertyLine() {
    // Given
    servicePointVersionRepository.save(ServicePointTestData.createServicePointVersionWithCountryBorder());
    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .operatingPointTechnicalTimetableTypes(List.of(OperatingPointTechnicalTimetableType.PROPERTY_LINE))
                .build()).build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(0);
  }

  @Test
  void shouldFindBernWylereggByUicCoutryCodes85and10() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .uicCountryCodes(Arrays.asList(85, 10))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindBernWylereggByCountryCodes55and85() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .uicCountryCodes(Arrays.asList(55, 10))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByIsoCountryCodeCH() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .isoCountryCodes(List.of("CH", "DE"))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindBernWylereggByIsoCountryCodeHU() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .isoCountryCodes(List.of("HU", "DE"))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindBernWylereggByIsoCountryCodeCHUicCountryCodeAndNumberShort() {
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .isoCountryCodes(List.of("CH"))
                .uicCountryCodes(List.of(85))
                .numbersShort(List.of(89008))
                .build()).build());
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Bern, Wyleregg");
  }

  @Test
  void shouldNotFindAnyCountryByIsoCountryCodeWhenThereIsNoServicePointGeolocation() {
    // Given
    servicePointVersionRepository.deleteAll();
    servicePointVersionRepository.save(ServicePointTestData.createServicePointVersionWithoutServicePointGeolocation());
    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .isoCountryCodes(List.of("CH", "DE"))
                .build()).build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isZero();
  }

  @Test
  void shouldFindOnlyOneServicePointForServicePointWithMultipleMeanOfTransports() {
    // Given
    servicePointVersionRepository.deleteAll();
    servicePointVersionRepository.save(ServicePointTestData.createServicePointVersionWithMultipleMeanOfTransport());
    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder().pageable(Pageable.unpaged())
            .servicePointRequestParams(ServicePointRequestParams.builder()
                .uicCountryCodes(Arrays.asList(85))
                .build()).build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(1);
    assertThat(servicePointVersions.getContent().get(0).getDesignationOfficial()).isEqualTo("Flüh Grenze");
  }

  @Test
  void shouldSortById() {
    // Given
    ServicePointVersion countryBorder = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersionWithCountryBorder());
    assertThat(servicePointVersion.getId()).isLessThan(countryBorder.getId());

    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(
            ServicePointSearchRestrictions.builder()
                .servicePointRequestParams(ServicePointRequestParams.builder().build())
                .pageable(PageRequest.of(0, 20,
                    Sort.by("id"))).build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(2);
    assertThat(servicePointVersions.getContent().get(0).getId()).isEqualTo(servicePointVersion.getId());
    assertThat(servicePointVersions.getContent().get(1).getId()).isEqualTo(countryBorder.getId());

    servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder()
            .servicePointRequestParams(ServicePointRequestParams.builder().build())
            .pageable(PageRequest.of(0, 20, Sort.by("id").descending()))
            .build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(2);
    assertThat(servicePointVersions.getContent().get(0).getId()).isEqualTo(countryBorder.getId());
    assertThat(servicePointVersions.getContent().get(1).getId()).isEqualTo(servicePointVersion.getId());
  }

  @Test
  void shouldSortByNumber() {
    // Given
    ServicePointVersion countryBorder = servicePointVersionRepository.save(
        ServicePointTestData.createServicePointVersionWithCountryBorder());

    assertThat(servicePointVersion.getNumber().getValue()).isGreaterThan(countryBorder.getNumber().getValue());

    // When
    Page<ServicePointVersion> servicePointVersions =
        servicePointService.findAll(
            ServicePointSearchRestrictions.builder()
                .servicePointRequestParams(ServicePointRequestParams.builder().build())
                .pageable(PageRequest.of(0, 20,
                    Sort.by("number"))).build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(2);
    assertThat(servicePointVersions.getContent().get(0).getId()).isEqualTo(countryBorder.getId());
    assertThat(servicePointVersions.getContent().get(1).getId()).isEqualTo(servicePointVersion.getId());

    servicePointVersions =
        servicePointService.findAll(ServicePointSearchRestrictions.builder()
            .servicePointRequestParams(ServicePointRequestParams.builder().build())
            .pageable(PageRequest.of(0, 20, Sort.by("number").descending()))
            .build());
    // Then
    assertThat(servicePointVersions.getTotalElements()).isEqualTo(2);
    assertThat(servicePointVersions.getContent().get(0).getId()).isEqualTo(servicePointVersion.getId());
    assertThat(servicePointVersions.getContent().get(1).getId()).isEqualTo(countryBorder.getId());
  }
}