package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class SectorVersionRepositoryTest {

  private final SectorVersionRepository sectorVersionRepository;

  @Autowired
  SectorVersionRepositoryTest(SectorVersionRepository sectorVersionRepository) {
    this.sectorVersionRepository = sectorVersionRepository;
  }

  @Test
  void shouldSaveSectorVersion() {
    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();

    // when
    SectorVersion savedVersion = sectorVersionRepository.save(sectorVersion);

    // then
    assertThat(savedVersion.getId()).isNotNull();
  }

  @Test
  void shouldFindAllBySloidOrderByValidFrom() {
    // given
    SectorVersion sectorVersion1 = SectorTestData.getBasicSectorVersion();
    SectorVersion sectorVersion2 = SectorTestData.getBasicSectorVersion();
    sectorVersion2.setValidFrom(LocalDate.of(2020, 1, 1));
    sectorVersion2.setValidTo(LocalDate.of(2022, 1, 1));

    SectorVersion sectorVersion3 = SectorTestData.getBasicSectorVersion();
    sectorVersion3.setSloid("ch:1:sloid:7000:321:431");

    sectorVersionRepository.save(sectorVersion1);
    sectorVersionRepository.save(sectorVersion2);
    sectorVersionRepository.save(sectorVersion3);

    // when
    List<SectorVersion> found = sectorVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:sector:1");

    // then
    assertThat(found).hasSize(2);
    assertThat(found.get(0).getSloid()).isEqualTo("ch:1:sloid:sector:1");
    assertThat(found.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(found.get(1).getSloid()).isEqualTo("ch:1:sloid:sector:1");
    assertThat(found.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
  }
}
