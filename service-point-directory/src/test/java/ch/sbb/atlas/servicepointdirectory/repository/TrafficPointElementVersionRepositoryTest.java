package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class TrafficPointElementVersionRepositoryTest {

  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
   TrafficPointElementVersionRepositoryTest(
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
        .designationOperational("Betriebliche Bezeich")
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .sloid("ch:1:sloid:7000:123:123")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
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
        .designationOperational("Betriebliche Bezeich")
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .sloid("ch:1:sloid:7000:123:123")
        .parentSloid("ch:1:sloid:7000:123:123")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
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
        .east(2600037.945)
        .north(1199749.812)
        .height(2540.21)
        .build();

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeich")
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .trafficPointElementGeolocation(trafficPointElementGeolocation)
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .sloid("ch:1:sloid:7000:123:123")
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

  @Test
  void shouldFindAllBySloidOrderByValidFrom() {
    // given
    TrafficPointElementVersion trafficPointElementVersion1 = TrafficPointTestData.getBasicTrafficPoint();
    TrafficPointElementVersion trafficPointElementVersion2 = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementVersion2.setValidFrom(LocalDate.of(2020, 1, 1));
    trafficPointElementVersion2.setValidTo(LocalDate.of(2022, 1, 1));

    TrafficPointElementVersion trafficPointElementVersion3 = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementVersion3.setSloid("ch:1:sloid:7000:321:431");
    trafficPointElementVersion3.setServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));

    trafficPointElementVersionRepository.save(trafficPointElementVersion1);
    trafficPointElementVersionRepository.save(trafficPointElementVersion2);
    trafficPointElementVersionRepository.save(trafficPointElementVersion3);

    // when
    List<TrafficPointElementVersion> found = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:89108:123:123");

    // then
    assertThat(found).hasSize(2);
    assertThat(found.get(0).getSloid()).isEqualTo("ch:1:sloid:89108:123:123");
    assertThat(found.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(found.get(1).getSloid()).isEqualTo("ch:1:sloid:89108:123:123");
    assertThat(found.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
  }

  @Test
  void shouldExistBySloid() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion
        .builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeich")
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .sloid("ch:1:sloid:7000:123:123")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    trafficPointElementVersionRepository.save(trafficPointElementVersion);

    // when
    boolean result = trafficPointElementVersionRepository.existsBySloid("ch:1:sloid:7000:123:123");

    // then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotExistBySloid() {
    // given
    // when
    boolean result = trafficPointElementVersionRepository.existsBySloid("ch:1:sloid:123");

    // then
    assertThat(result).isFalse();
  }
}
