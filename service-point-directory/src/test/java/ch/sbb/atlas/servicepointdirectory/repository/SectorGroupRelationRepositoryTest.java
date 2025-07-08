package ch.sbb.atlas.servicepointdirectory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.model.SectorGroupRelationId;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class SectorGroupRelationRepositoryTest {

  private final SectorGroupRelationRepository sectorGroupRelationRepository;

  @Autowired
  public SectorGroupRelationRepositoryTest(SectorGroupRelationRepository sectorGroupRelationRepository) {
    this.sectorGroupRelationRepository = sectorGroupRelationRepository;
  }

  @Test
  void shouldSaveAndFindBySectorGroupSloid() {
    // Given
    String groupSloid1 = "grp:1";
    String sectorSloidA = "sec:A";
    String sectorSloidB = "sec:B";

    SectorGroupRelation relation1 = new SectorGroupRelation(
        new SectorGroupRelationId(groupSloid1, sectorSloidA)
    );
    SectorGroupRelation relation2 = new SectorGroupRelation(
        new SectorGroupRelationId(groupSloid1, sectorSloidB)
    );
    SectorGroupRelation relation3 = new SectorGroupRelation(
        new SectorGroupRelationId("grp:2", "sec:C")
    );

    sectorGroupRelationRepository.save(relation1);
    sectorGroupRelationRepository.save(relation2);
    sectorGroupRelationRepository.save(relation3);

    // When
    Set<SectorGroupRelation> results =
        sectorGroupRelationRepository.findBySectorGroupRelationIdSectorGroupSloid(groupSloid1);

    // Then
    assertThat(results)
        .hasSize(2)
        .extracting(r -> r.getSectorGroupRelationId().getSectorSloid())
        .containsExactlyInAnyOrder(sectorSloidA, sectorSloidB);
  }

}
