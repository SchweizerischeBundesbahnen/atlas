package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class SectorGroupVersionRepositoryTest {

  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final SectorGroupRelationRepository sectorGroupRelationRepository;

  @Autowired
  SectorGroupVersionRepositoryTest(SectorGroupVersionRepository sectorGroupVersionRepository,
      SectorGroupRelationRepository sectorGroupRelationRepository) {
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
    this.sectorGroupRelationRepository = sectorGroupRelationRepository;
  }

  @BeforeEach
  void cleanUp() {
    sectorGroupVersionRepository.deleteAll();
    sectorGroupRelationRepository.deleteAll();
  }

  @Test
  void shouldSaveSectorGroupVersion() {
    sectorGroupVersionRepository.deleteAll();

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
        .status(Status.VALIDATED)
        .build();

    SectorVersion sectorVersion2 = SectorVersion.builder()
        .sloid("ch:1:sloid:2")
        .trafficPointSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Test")
        .east(1.0)
        .north(1.0)
        .edgeHeight(1.0)
        .spatialReference(SpatialReference.LV95)
        .status(Status.VALIDATED)
        .build();

    List<SectorVersion> list = List.of(sectorVersion, sectorVersion2);

    SectorGroupVersion sectorGroupVersion = SectorGroupVersion.builder()
        .sloid("ch:1:sloid:1")
        .trafficPointSloid("ch:1:sloid:1")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .designation("Test")
        .status(Status.VALIDATED)
        .build();

    SectorGroupVersion savedVersion = sectorGroupVersionRepository.save(sectorGroupVersion);

    for (SectorVersion savedSectorVersion : list) {
      SectorGroupRelationId sectorGroupRelationId = SectorGroupRelationId.builder()
          .sectorGroupSloid("ch:1:sloid:1")
          .sectorSloid(savedSectorVersion.getSloid())
          .build();
      sectorGroupRelationRepository.saveAndFlush(
          SectorGroupRelation.builder().sectorGroupRelationId(sectorGroupRelationId).build());
    }

    List<SectorGroupRelation> relations = sectorGroupRelationRepository.findAll();

    // then
    assertThat(savedVersion.getId()).isNotNull();
    assertThat(relations).hasSize(2);
  }

  @Test
  void shouldFindAllBySloidOrderByValidFrom() {
    sectorGroupVersionRepository.deleteAll();
    // given
    SectorGroupVersion sectorGroup1 = SectorTestData.getBasicSectorGroupVersion();
    SectorGroupVersion sectorGroup2 = SectorTestData.getBasicSectorGroupVersion();
    sectorGroup2.setValidFrom(LocalDate.of(2020, 1, 1));
    sectorGroup2.setValidTo(LocalDate.of(2022, 1, 1));

    SectorGroupVersion sectorGroup3 = SectorTestData.getBasicSectorGroupVersion();
    sectorGroup3.setSloid("ch:1:sloid:7000:321:431");

    sectorGroupVersionRepository.save(sectorGroup1);
    sectorGroupVersionRepository.save(sectorGroup2);
    sectorGroupVersionRepository.save(sectorGroup3);

    // when
    List<SectorGroupVersion> found = sectorGroupVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:group:1");

    // then
    assertThat(found).hasSize(2);
    assertThat(found.get(0).getSloid()).isEqualTo("ch:1:sloid:group:1");
    assertThat(found.get(0).getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(found.get(1).getSloid()).isEqualTo("ch:1:sloid:group:1");
    assertThat(found.get(1).getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
  }
}
