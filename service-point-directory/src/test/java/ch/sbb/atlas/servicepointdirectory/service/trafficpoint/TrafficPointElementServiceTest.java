package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class TrafficPointElementServiceTest {

  // required for test functionality
  @MockBean
  private TrafficPointElementValidationService trafficPointElementValidationService;

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
    assertThat(trafficPointElementService.findBySloidOrderByValidFrom("ch:1:sloid:123")).hasSize(1);
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
                .sloid("ch:1:sloid:1400015:0:310240")
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
                .sloid("daniel hat ferien")
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
                .servicePointNumbers(List.of(1400015))
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
                .servicePointNumbers(List.of(8089107))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumberSboid() {
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
  void shouldNotFindByServicePointNumberSboid() {
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
  void shouldFindByServicePointNumberShortNumber() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .servicePointNumbersShort(List.of(1))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByServicePointNumberShortNumber() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .servicePointNumbersShort(List.of(55))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumberServicePointNumber() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .servicePointNumbers(List.of(14000158))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindByServicePointNumberServicePointNumber() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
            TrafficPointElementSearchRestrictions.builder()
                    .pageable(Pageable.unpaged())
                    .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                            .servicePointNumbers(List.of(74000158))
                            .build())
                    .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindByServicePointNumberUicCountryCode() {
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
  void shouldNotFindByServicePointNumberUicCountryCode() {
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
  void shouldFindByServicePointNumberParentSloid() {
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
  void shouldNotFindByServicePointNumberParentSloid() {
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
  void shouldFindByServicePointNumberFromDate() {
    // given
    servicePointVersionRepository.save(TrafficPointTestData.testServicePointForTrafficPoint());
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);
    LocalDate toDate = trafficPointElementVersion.getValidTo();
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
  void shouldNotFindByServicePointNumberFromDate() {
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
  void shouldFindByServicePointNumberToDate() {
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
  void shouldNotFindByServicePointNumberToDate() {
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
  void shouldFindByServicePointNumberOnDate() {
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
  void shouldNotFindByServicePointNumberOnDate() {
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

}
