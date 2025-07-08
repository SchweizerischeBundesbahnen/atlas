package ch.sbb.atlas.servicepointdirectory.service.sector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.relation.SectorGroupRelationId;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SectorNotExistingException;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupRelationRepository;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class SectorGroupServiceTest {

  private final SectorGroupService sectorGroupService;
  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final SectorGroupRelationRepository sectorGroupRelationRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  public SectorGroupServiceTest(SectorGroupService sectorGroupService, SectorGroupVersionRepository sectorGroupVersionRepository,
      SectorGroupRelationRepository sectorGroupRelationRepository, SectorVersionRepository sectorVersionRepository,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.sectorGroupService = sectorGroupService;
    this.sectorGroupVersionRepository = sectorGroupVersionRepository;
    this.sectorGroupRelationRepository = sectorGroupRelationRepository;
    this.sectorVersionRepository = sectorVersionRepository;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @AfterEach
  void cleanup() {
    sectorVersionRepository.deleteAll();
    sectorGroupRelationRepository.deleteAll();
    sectorGroupVersionRepository.deleteAll();
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  @Transactional
  void shouldMergeSectorGroup() {
    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    sectorGroupVersion = sectorGroupVersionRepository.save(sectorGroupVersion);

    SectorGroupVersion edited = SectorTestData.getBasicSectorGroupVersion();
    edited.setValidFrom(LocalDate.of(2020, 1, 2));
    edited.setValidTo(LocalDate.of(2025, 12, 31));
    edited.setVersion(sectorGroupVersion.getVersion());

    // when
    sectorGroupService.updateSectorGroup(sectorGroupVersion, edited);

    // then
    assertThat(sectorGroupService.findAllBySloidOrderByValidFrom("ch:1:sloid:group:1")).hasSize(1);
  }

  @Test
  void shouldReturnEmptyListWhenNoGroups() {
    assertThat(sectorGroupService.getSectorGroups()).isEmpty();
  }

  @Test
  void shouldReturnAllSectorGroups() {
    sectorGroupVersionRepository.save(SectorTestData.getBasicSectorGroupVersion());

    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    sectorGroupVersion.setSloid("ch:1:sloid:group:2");
    sectorGroupVersionRepository.save(sectorGroupVersion);

    List<SectorGroupVersionModel> all = sectorGroupService.getSectorGroups();

    assertThat(all).hasSize(2)
        .extracting(SectorGroupVersionModel::getSloid)
        .containsExactlyInAnyOrder(
            "ch:1:sloid:group:1",
            "ch:1:sloid:group:2"
        );
  }

  @Test
  void shouldGetSectorGroupModelsBySloid() {
    String sloid = "grp:42";

    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    sectorGroupVersion.setSloid(sloid);
    sectorGroupVersionRepository.save(sectorGroupVersion);

    List<SectorGroupVersionModel> models = sectorGroupService.getSectorGroup(sloid);

    assertThat(models)
        .extracting(SectorGroupVersionModel::getSloid)
        .containsExactly(
            "grp:42"
        );
  }

  @Test
  void shouldGetReadModelWithRelatedSectors() {
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());
    String sector1 = "sector:1:abc";
    String sector2 = "sector:2:abc";

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sector1)
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2030, 1, 1))
        .designation("dese")
        .build());

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sector2)
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 2, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build());

    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion()
        .toBuilder()
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .build();

    SectorGroupVersion savedGroup = sectorGroupVersionRepository.save(sectorGroupVersion);

    sectorGroupRelationRepository.save(new SectorGroupRelation(
        new SectorGroupRelationId(savedGroup.getSloid(), sector1)));
    sectorGroupRelationRepository.save(new SectorGroupRelation(
        new SectorGroupRelationId(savedGroup.getSloid(), sector2)));

    // When
    ReadSectorGroupVersionModel read = sectorGroupService.getSectorGroupVersion(savedGroup.getId());

    // Then
    assertThat(read.getSloid()).isEqualTo(savedGroup.getSloid());
    assertThat(read.getSectorVersions())
        .extracting(SectorVersionModel::getSloid)
        .containsExactlyInAnyOrder(sector1, sector2);
  }

  @Test
  void shouldCreateSectorGroupSuccessfully() {
    sectorGroupVersionRepository.deleteAll();
    sectorGroupRelationRepository.deleteAll();

    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());
    List<String> sloids = List.of("sector:A", "sector:B");
    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("sector:A")
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2045, 1, 1))
        .designation("hehe")
        .build());

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("sector:B")
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 2, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build());

    SectorGroupVersion toCreate = SectorTestData.getBasicSectorGroupVersion()
        .toBuilder()
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build();

    ReadSectorGroupVersionModel result =
        sectorGroupService.createSectorGroup(toCreate, sloids);

    assertThat(sectorGroupVersionRepository.findById(result.getId())).isPresent();
    assertThat(sectorGroupRelationRepository.findAll()).hasSize(2);
    assertThat(sectorGroupRelationRepository.findBySectorGroupRelationIdSectorGroupSloid(toCreate.getSloid()))
        .extracting(r -> r.getSectorGroupRelationId().getSectorSloid())
        .containsExactlyInAnyOrder("sector:A", "sector:B");
  }

  @Test
  void shouldThrowWhenCreatingGroupWithLessThanTwoSectors() {
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());
    List<String> sloids = List.of("sector:A", "sector:B");

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("sector:A")
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2045, 1, 1))
        .designation("hehe")
        .build());

    SectorGroupVersion toCreate = SectorTestData.getBasicSectorGroupVersion()
        .toBuilder()
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build();

    assertThatThrownBy(() ->
        sectorGroupService.createSectorGroup(toCreate, sloids)
    ).isInstanceOf(SectorNotExistingException.class);
  }

  @Test
  void shouldThrowWhenCreatingGroupWithMissingTrafficPoint() {
    String sloid = "sector:1";
    String sloid2 = "sector:2";
    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sloid)
        .trafficPointSloid("not:existing")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2045, 1, 1))
        .designation("hehe")
        .build());

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sloid2)
        .trafficPointSloid("not:existing")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2045, 1, 1))
        .designation("hehe")
        .build());

    SectorGroupVersion toCreate = SectorTestData.getBasicSectorGroupVersion()
        .toBuilder()
        .trafficPointSloid("existing:trafficpoint")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build();

    assertThatThrownBy(() ->
        sectorGroupService.createSectorGroup(toCreate, List.of(sloid, sloid2))
    ).isInstanceOf(SloidNotFoundException.class);
  }

  @Test
  void shouldThrowWhenCreatingGroupWithMismatchedTrafficPoints() {
    TrafficPointElementVersion trafficPointElementVersion1 = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());

    TrafficPointElementVersion trafficPointElementVersion2 = TrafficPointTestData.getWylerEggPlatform();
    trafficPointElementVersionRepository.save(trafficPointElementVersion2);

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("s1")
        .trafficPointSloid(trafficPointElementVersion1.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build());

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("s2").trafficPointSloid(trafficPointElementVersion2.getSloid())
        .validFrom(LocalDate.of(2022, 2, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build());

    SectorGroupVersion toCreate = SectorTestData.getBasicSectorGroupVersion()
        .toBuilder()
        .trafficPointSloid(trafficPointElementVersion1.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("hehe")
        .build();

    assertThatThrownBy(() ->
        sectorGroupService.createSectorGroup(toCreate, List.of("s1", "s2"))
    ).isInstanceOf(SloidsNotEqualException.class);
  }

  @Test
  @Transactional
  void shouldUpdateSectorGroupAndIncrementVersion() {
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());

    SectorGroupVersion sectorGroupVersion = sectorGroupVersionRepository.save(
        SectorTestData.getBasicSectorGroupVersion().toBuilder()
            .sloid("sector:A")
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2024, 1, 1))
            .designation("hehe")
            .trafficPointSloid(trafficPointElementVersion.getSloid())
            .build()
    );

    SectorGroupVersion edited = sectorGroupVersion.toBuilder()
        .validFrom(sectorGroupVersion.getValidFrom().plusDays(1))
        .designation("new des")
        .version(0)
        .build();

    sectorGroupService.updateSectorGroup(sectorGroupVersion, edited);

    List<SectorGroupVersion> versions =
        sectorGroupService.findAllBySloidOrderByValidFrom(sectorGroupVersion.getSloid());
    assertThat(versions).hasSize(2);
    assertThat(versions.get(1).getVersion()).isEqualTo(sectorGroupVersion.getVersion() + 1);
  }

  @Test
  @Transactional
  void shouldThrowWhenUpdatingGroupWithStaleVersion() {
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());
    SectorGroupVersion sectorGroupVersion = sectorGroupVersionRepository.save(
        SectorTestData.getBasicSectorGroupVersion().toBuilder()
            .sloid("sector:A")
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2024, 1, 1))
            .designation("hehe")
            .version(1)
            .trafficPointSloid(trafficPointElementVersion.getSloid())
            .build()
    );

    SectorGroupVersion edited = sectorGroupVersion.toBuilder()
        .validFrom(sectorGroupVersion.getValidFrom().plusDays(1))
        .designation("new des")
        .version(99)
        .build();

    assertThatThrownBy(() -> sectorGroupService.updateSectorGroup(sectorGroupVersion, edited))
        .isInstanceOf(StaleObjectStateException.class);
  }
}
