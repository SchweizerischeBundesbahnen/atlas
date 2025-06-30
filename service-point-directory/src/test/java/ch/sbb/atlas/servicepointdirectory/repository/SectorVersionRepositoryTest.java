package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

  @Autowired
  private JdbcTemplate jdbc;

  @Autowired
  private EntityManager em;

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

    SectorVersion sectorVersion2 = SectorVersion.builder()
        .sloid("ch:1:sloid:2")
        .trafficPointSloid("ch:1:sloid:2")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Test 2")
        .east(1.0)
        .north(1.0)
        .edgeHeight(1.0)
        .spatialReference(SpatialReference.LV95)
        .build();

    SectorVersion savedVersion = sectorVersionRepository.saveAndFlush(sectorVersion);
    SectorVersion savedVersion2 = sectorVersionRepository.saveAndFlush(sectorVersion2);

    SectorGroupVersion sectorGroupVersion1 = SectorGroupVersion.builder()
        .sloid("ch:1:sloid:1")
        .trafficPointSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Test")
        .sectorVersions(List.of(savedVersion, savedVersion2))
        .build();

    SectorGroupVersion result = sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion1);

    em.flush();
    em.clear();

    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT * FROM sector_group_relations");

    // then
    assertThat(result.getId()).isNotNull();
    assertThat(rows.size()).isEqualTo(2);
  }

}
