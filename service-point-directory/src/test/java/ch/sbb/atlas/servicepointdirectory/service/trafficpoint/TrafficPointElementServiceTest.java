package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static ch.sbb.atlas.servicepointdirectory.TrafficPointTestData.SERVICE_POINT_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@IntegrationTest
 class TrafficPointElementServiceTest {

  @MockBean
  private CrossValidationService crossValidationService;

  private final TrafficPointElementService trafficPointElementService;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;
  private final ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  TrafficPointElementServiceTest(TrafficPointElementService trafficPointElementService,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.trafficPointElementService = trafficPointElementService;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @AfterEach
  void cleanup() {
    trafficPointElementVersionRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldMergeTrafficPoint() {
    // given
    doNothing().when(crossValidationService).validateServicePointNumberExists(any());

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    TrafficPointElementVersion edited = TrafficPointTestData.getBasicTrafficPoint();
    edited.setValidFrom(LocalDate.of(2024, 1, 2));
    edited.setValidTo(LocalDate.of(2024, 12, 31));
    edited.getTrafficPointElementGeolocation().setCreationDate(null);
    edited.getTrafficPointElementGeolocation().setCreator(null);
    edited.getTrafficPointElementGeolocation().setEditionDate(null);
    edited.getTrafficPointElementGeolocation().setEditor(null);

    // when
    trafficPointElementService.updateTrafficPointElementVersion(trafficPointElementVersion, edited);

    // then
    assertThat(trafficPointElementService.findBySloidOrderByValidFrom("ch:1:sloid:123:123:123")).hasSize(1);
  }

  @Test
  void shouldFindBySloids() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
        TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .sloids(List.of("ch:1:sloid:1400015:0:310240"))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindBySloids() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
        TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .sloids(List.of("daniel hat ferien"))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByParentSloids() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .parentsloids(List.of("ch:1:sloid:1400015:310240"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByParentSloids() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .parentsloids(List.of("888"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumberUicCountryCodes() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .uicCountryCodes(List.of("14"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByServicePointNumberUicCountryCodes() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .uicCountryCodes(List.of("11"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumberShortNumbers() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .servicePointNumbersShort(List.of("1"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByServicePointNumberShortNumbers() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .servicePointNumbersShort(List.of("55"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumber() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
        TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .servicePointNumbers(List.of("1400015"))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByServicePointNumber() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
        TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .servicePointNumbers(List.of("8089107"))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumberSboids() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
        TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .sboids(List.of("somesboid"))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByServicePointNumberSboids() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .sboids(List.of("abrakadabra"))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByFromDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);
    LocalDate fromDate = trafficPointElementVersion.getValidFrom().minusDays(1);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .fromDate(fromDate)
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByFromDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);
    LocalDate fromDate = trafficPointElementVersion.getValidFrom().plusDays(1);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .fromDate(fromDate)
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByToDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);
    LocalDate toDate = trafficPointElementVersion.getValidTo().plusDays(1);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .toDate(toDate)
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByToDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);
    LocalDate toDate = trafficPointElementVersion.getValidTo().minusDays(1);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .toDate(toDate)
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByOnDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .validOn(LocalDate.of(2021, 1, 1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByOnDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .validOn(LocalDate.of(2999, 1, 1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByCreatedAfter() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .createdAfter(trafficPointElementVersion.getCreationDate().minusSeconds(1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNoFindByCreatedAfter() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .createdAfter(trafficPointElementVersion.getCreationDate().plusSeconds(1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByModifiedAfter() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .modifiedAfter(trafficPointElementVersion.getEditionDate().minusSeconds(1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNoFindByModifiedAfter() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .modifiedAfter(trafficPointElementVersion.getEditionDate().plusSeconds(1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByMultipleSearch() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);
    LocalDate fromDate = trafficPointElementVersion.getValidFrom().minusDays(1);
    LocalDate toDate = trafficPointElementVersion.getValidTo().plusDays(1);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .sloids(List.of("ch:1:sloid:1400015:0:310240"))
                            .parentsloids(List.of("ch:1:sloid:1400015:310240"))
                            .uicCountryCodes(List.of("14"))
                            .servicePointNumbers(List.of("1400015"))
                            .servicePointNumbersShort(List.of("1"))
                            .sboids(List.of("somesboid"))
                            .fromDate(fromDate)
                            .toDate(toDate)
                            .validOn(LocalDate.of(2021, 1, 1))
                            .createdAfter(trafficPointElementVersion.getCreationDate().minusSeconds(1))
                            .modifiedAfter(trafficPointElementVersion.getEditionDate().minusSeconds(1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldLoadPlatformTableCorrectly() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());

    // Element 1
    trafficPointElementVersionRepository.save(TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .sloid("ch:1:sloid:123:123:123")
        .servicePointNumber(SERVICE_POINT_NUMBER)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2020, 1, 6))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());
    trafficPointElementVersionRepository.save(TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung 2")
        .sloid("ch:1:sloid:123:123:123")
        .servicePointNumber(SERVICE_POINT_NUMBER)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.of(2021, 1, 6))
        .validTo(LocalDate.of(2021, 12, 31))
        .build());

    // Element 2
    trafficPointElementVersionRepository.save(TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .sloid("ch:1:sloid:345:345:345")
        .servicePointNumber(SERVICE_POINT_NUMBER)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.now().minusDays(15))
        .validTo(LocalDate.now().minusDays(7))
        .build());
    trafficPointElementVersionRepository.save(TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung 2")
        .sloid("ch:1:sloid:345:345:345")
        .servicePointNumber(SERVICE_POINT_NUMBER)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .validFrom(LocalDate.now().minusDays(5))
        .validTo(LocalDate.now().plusDays(5))
        .build());

    // when
    Container<ReadTrafficPointElementVersionModel> result =
        trafficPointElementService.getTrafficPointElementsByServicePointNumber(SERVICE_POINT_NUMBER.getNumber(),
            PageRequest.of(0, 10, Sort.by("sloid")),
            TrafficPointElementType.BOARDING_PLATFORM);

    // then
    assertThat(result.getObjects()).hasSize(2);

    ReadTrafficPointElementVersionModel first = result.getObjects().get(0);
    assertThat(first.getDesignation()).isEqualTo("Bezeichnung 2");
    assertThat(first.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 6));
    assertThat(first.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));

    ReadTrafficPointElementVersionModel second = result.getObjects().get(1);
    assertThat(second.getDesignation()).isEqualTo("Bezeichnung 2");
    assertThat(second.getValidFrom()).isEqualTo(LocalDate.now().minusDays(15));
    assertThat(second.getValidTo()).isEqualTo(LocalDate.now().plusDays(5));
  }

}
