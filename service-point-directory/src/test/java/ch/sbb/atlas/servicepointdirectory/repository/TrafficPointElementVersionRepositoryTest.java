package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TrafficPointElementVersionRepositoryTest {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  public TrafficPointElementVersionRepositoryTest(
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @AfterEach
  void tearDown() {
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  void shouldSaveTrafficPointElementVersionWithoutParent() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeichnung")
        .servicePointNumber(ServicePointNumber.of(85070003))
        .sloid("ch:1:sloid:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(
        trafficPointElementVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.hasGeolocation()).isFalse();
  }

  @Test
  void shouldSaveTrafficPointElementVersionWithParent() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeichnung")
        .servicePointNumber(ServicePointNumber.of(85070003))
        .sloid("ch:1:sloid:123")
        .parentSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(
        trafficPointElementVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getParentSloid()).isNotNull();
  }

  @Test
  void shouldSaveTrafficPointElementVersionWithGeolocation() {
    // given
    TrafficPointElementGeolocation trafficPointElementGeolocation = TrafficPointElementGeolocation
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
        .build();

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeichnung")
        .servicePointNumber(ServicePointNumber.of(85070003))
        .trafficPointElementGeolocation(trafficPointElementGeolocation)
        .sloid("ch:1:sloid:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    trafficPointElementGeolocation.setTrafficPointElementVersion(trafficPointElementVersion);

    // when
    TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(
        trafficPointElementVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.hasGeolocation()).isTrue();
    assertThat(savedVersion.getTrafficPointElementGeolocation().getId()).isNotNull();
  }
}
