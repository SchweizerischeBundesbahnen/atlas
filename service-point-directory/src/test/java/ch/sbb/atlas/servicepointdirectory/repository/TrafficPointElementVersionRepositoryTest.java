package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
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
  public TrafficPointElementVersionRepositoryTest(TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @AfterEach
  void tearDown() {
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  void shouldSaveTrafficPointElementVersionWithoutParent() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeichnung")
        .servicePointNumber(1)
        .sloid("ch:1:sloid:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(trafficPointElementVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.isVirtuell()).isTrue();
  }

  @Test
  void shouldSaveTrafficPointElementVersionWithParent() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeichnung")
        .servicePointNumber(1)
        .sloid("ch:1:sloid:123")
        .parentSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    // when
    TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(trafficPointElementVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.getParentSloid()).isNotNull();
  }

  @Test
  void shouldSaveTrafficPointElementVersionWithGeolocation() {
    // given
    TrafficPointElementGeolocation trafficPointElementGeolocation = TrafficPointElementGeolocation.builder()
        .source_spatial_ref(1)
        .lv03east(600037.945)
        .lv03north(199749.812)
        .lv95east(2600037.945)
        .lv95north(1199749.812)
        .wgs84east(7.43913089)
        .wgs84north(46.94883229)
        .height(540.2)
        .isoCountryCode(Country.SWITZERLAND.getIsoCode())
        .swissCantonFsoNumber(5)
        .swissCantonName("Bern")
        .swissCantonNumber(5)
        .swissDistrictName("Bern")
        .swissDistrictNumber(5)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .build();

    TrafficPointElementVersion trafficPointElementVersion = TrafficPointElementVersion.builder()
        .designation("Bezeichnung")
        .designationOperational("Betriebliche Bezeichnung")
        .servicePointNumber(1)
        .trafficPointElementGeolocation(trafficPointElementGeolocation)
        .sloid("ch:1:sloid:123")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    trafficPointElementGeolocation.setTrafficPointElementVersion(trafficPointElementVersion);

    // when
    TrafficPointElementVersion savedVersion = trafficPointElementVersionRepository.save(trafficPointElementVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(savedVersion.isVirtuell()).isFalse();
    assertThat(savedVersion.getTrafficPointElementGeolocation().getId()).isNotNull();
  }
}