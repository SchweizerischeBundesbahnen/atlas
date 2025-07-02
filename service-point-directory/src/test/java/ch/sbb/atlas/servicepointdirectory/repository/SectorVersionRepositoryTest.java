package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.model.controller.IntegrationTest;
import jakarta.persistence.EntityManager;
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

  }

}
