package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class SectorVersionRepositoryTest {

  private final SectorVersionRepository sectorVersionRepository;
  private final SectorGroupVersionRepository sectorGroupVersionRepository;

  @Autowired
  SectorVersionRepositoryTest(SectorVersionRepository sectorVersionRepository,
      SectorGroupVersionRepository sectorGroupVersionRepository) {
    this.sectorVersionRepository = sectorVersionRepository;
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
  }

  @Test
  void shouldSaveSectorVersion() {
    SectorVersion sectorVersion = SectorVersion.builder()
        .sloid("ch:1:sloid:1")
        .trafficPointSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Test")
        .east(1.0)
        .north(1.0)
        .edgeHeight(1.0)
        .spatialReference(SpatialReference.LV95)
        .build();

    SectorVersion savedVersion = sectorVersionRepository.saveAndFlush(sectorVersion);

    SectorGroupVersion sectorGroupVersion1 = SectorGroupVersion.builder()
        .sloid("ch:1:sloid:1")
        .trafficPointSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Test")
        .sectorVersions(List.of(savedVersion))
        .build();

    SectorGroupVersion result = sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion1);

    // then
    assertThat(result.getId()).isNotNull();
  }

}
