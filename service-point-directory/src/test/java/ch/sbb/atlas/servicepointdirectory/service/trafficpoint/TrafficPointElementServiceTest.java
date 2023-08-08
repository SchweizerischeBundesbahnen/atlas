package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    // when
    TrafficPointElementSearchRestrictions searchRestrictions =
        TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .sloid("ch:1:sloid:123")
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindBySloids() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
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
                .sboids(List.of("sanjas lieblingsjoghurt"))
                .build())
            .build();
    Page<TrafficPointElementVersion> result = trafficPointElementService.findAll(searchRestrictions);

    // then
    assertThat(result.getContent()).isEmpty();
  }
}
