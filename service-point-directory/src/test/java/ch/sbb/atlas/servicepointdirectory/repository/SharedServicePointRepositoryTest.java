package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class SharedServicePointRepositoryTest {

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  private SharedServicePointRepository sharedServicePointRepository;

  @AfterEach
  void tearDown() {
    trafficPointElementVersionRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldFindServicePoints() {
    servicePointVersionRepository.saveAndFlush(ServicePointTestData.getBernWyleregg());

    Set<SharedServicePointVersionModel> sharedServicePoints = sharedServicePointRepository.getAllServicePoints();
    assertThat(sharedServicePoints).hasSize(1);
    SharedServicePointVersionModel servicePoint = sharedServicePoints.iterator().next();
    assertThat(servicePoint.getServicePointSloid()).isNotEmpty();
    assertThat(servicePoint.getSboids()).isNotEmpty();
    assertThat(servicePoint.getTrafficPointSloids()).isEmpty();
    assertThat(servicePoint.isStopPoint()).isTrue();
  }

  @Test
  void shouldFindServicePointWithMultipleMeansOfTransport() {
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    bernWyleregg.setMeansOfTransport(Set.of(MeanOfTransport.BOAT, MeanOfTransport.BUS));
    servicePointVersionRepository.saveAndFlush(bernWyleregg);

    Set<SharedServicePointVersionModel> sharedServicePoints = sharedServicePointRepository.getAllServicePoints();
    assertThat(sharedServicePoints).hasSize(1);
    SharedServicePointVersionModel servicePoint = sharedServicePoints.iterator().next();
    assertThat(servicePoint.getServicePointSloid()).isNotEmpty();
    assertThat(servicePoint.getSboids()).isNotEmpty();
    assertThat(servicePoint.getTrafficPointSloids()).isEmpty();
    assertThat(servicePoint.isStopPoint()).isTrue();
  }

  @Test
  void shouldFindServicePointsAsNonStopPointWithMeanOfTransportUnknown() {
    servicePointVersionRepository.saveAndFlush(
        ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion());

    Set<SharedServicePointVersionModel> sharedServicePoints = sharedServicePointRepository.getAllServicePoints();
    assertThat(sharedServicePoints).isNotEmpty().hasSize(1);
    assertThat(sharedServicePoints.iterator().next().isStopPoint()).isFalse();
  }

  @Test
  void shouldFindServicePointsAsNonStopPointWithNoMeanOfTransport() {
    ServicePointVersion version = ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
    version.setMeansOfTransport(Collections.emptySet());
    servicePointVersionRepository.saveAndFlush(version);

    Set<SharedServicePointVersionModel> sharedServicePoints = sharedServicePointRepository.getAllServicePoints();
    assertThat(sharedServicePoints).isNotEmpty().hasSize(1);
    assertThat(sharedServicePoints.iterator().next().isStopPoint()).isFalse();
  }

  @Test
  void shouldFindServicePointWithTrafficPoint() {
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    servicePointVersionRepository.saveAndFlush(bernWyleregg);
    TrafficPointElementVersion trafficPoint = TrafficPointTestData.getWylerEggPlatform();
    trafficPointElementVersionRepository.saveAndFlush(trafficPoint);

    Set<SharedServicePointVersionModel> sharedServicePoints =
        sharedServicePointRepository.getServicePoints(Set.of(bernWyleregg.getNumber()));
    assertThat(sharedServicePoints).hasSize(1);
    SharedServicePointVersionModel servicePoint = sharedServicePoints.iterator().next();
    assertThat(servicePoint.getServicePointSloid()).isEqualTo("ch:1:sloid:89008");
    assertThat(servicePoint.getSboids()).containsExactly("ch:1:sboid:100626");
    assertThat(servicePoint.getTrafficPointSloids()).containsExactly("ch:1:sloid:89008:123:123");
  }

  @Test
  void shouldFindMultipleVersionsAndBeStopPointTrueIfVersionOneHasMoTUnknown() {
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    bernWyleregg.setMeansOfTransport(Set.of(MeanOfTransport.UNKNOWN));
    servicePointVersionRepository.saveAndFlush(bernWyleregg);

    ServicePointVersion bernWylereggV2 = ServicePointTestData.getBernWyleregg();
    bernWylereggV2.setValidFrom(LocalDate.of(2022, 1, 1));
    bernWylereggV2.setValidTo(LocalDate.of(2022, 3, 31));
    servicePointVersionRepository.saveAndFlush(bernWylereggV2);

    TrafficPointElementVersion trafficPoint = TrafficPointTestData.getWylerEggPlatform();
    trafficPointElementVersionRepository.saveAndFlush(trafficPoint);

    Set<SharedServicePointVersionModel> sharedServicePoints =
        sharedServicePointRepository.getServicePoints(Set.of(bernWyleregg.getNumber()));

    assertThat(sharedServicePoints).hasSize(1);
    SharedServicePointVersionModel servicePoint = sharedServicePoints.iterator().next();
    assertThat(servicePoint.getServicePointSloid()).isEqualTo("ch:1:sloid:89008");
    assertThat(servicePoint.isStopPoint()).isTrue();
  }

  @Test
  void shouldFindMultipleVersionsAndBeStopPointTrueOnSecondVersionNoMeansOfTransport() {
    ServicePointVersion bernWyleregg = ServicePointTestData.getBernWyleregg();
    servicePointVersionRepository.saveAndFlush(bernWyleregg);

    ServicePointVersion bernWylereggV2 = ServicePointTestData.getBernWyleregg();
    bernWylereggV2.setValidFrom(LocalDate.of(2022, 1, 1));
    bernWylereggV2.setValidTo(LocalDate.of(2022, 3, 31));
    bernWylereggV2.setMeansOfTransport(Collections.emptySet());
    servicePointVersionRepository.saveAndFlush(bernWylereggV2);

    TrafficPointElementVersion trafficPoint = TrafficPointTestData.getWylerEggPlatform();
    trafficPointElementVersionRepository.saveAndFlush(trafficPoint);

    Set<SharedServicePointVersionModel> sharedServicePoints =
        sharedServicePointRepository.getServicePoints(Set.of(bernWyleregg.getNumber()));

    assertThat(sharedServicePoints).hasSize(1);
    SharedServicePointVersionModel servicePoint = sharedServicePoints.iterator().next();
    assertThat(servicePoint.getServicePointSloid()).isEqualTo("ch:1:sloid:89008");
    assertThat(servicePoint.isStopPoint()).isTrue();
  }
}