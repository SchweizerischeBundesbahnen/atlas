package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.SectorNotExistingException;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.SectorValidityException;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupRelationRepository;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.repository.SectorGroupVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.SectorValidationService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class SectorGroupServiceTest {

  @MockitoBean
  private LocationService locationService;

  private final SectorGroupService sectorGroupService;
  private final SectorGroupVersionRepository sectorGroupVersionRepository;
  private final SectorGroupRelationRepository sectorGroupRelationRepository;
  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  public SectorGroupServiceTest(SectorGroupService sectorGroupService, SectorGroupVersionRepository sectorGroupVersionRepository,
      SectorGroupRelationRepository sectorGroupRelationRepository, SectorVersionRepository sectorVersionRepository,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository, SectorValidationService sharedSectorService) {
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
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());

    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    sectorGroupVersion.setTrafficPointSloid(trafficPointElementVersion.getSloid());
    sectorGroupVersion = sectorGroupVersionRepository.save(sectorGroupVersion);

    SectorGroupVersion edited = SectorTestData.getBasicSectorGroupVersion();
    edited.setValidFrom(LocalDate.of(2023, 1, 2));
    edited.setValidTo(LocalDate.of(2023, 12, 31));
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

    List<SectorGroupVersion> versions = sectorGroupService.findAllBySloidOrderByValidFrom(sloid);

    assertThat(versions)
        .extracting(SectorGroupVersion::getSloid)
        .containsExactly(
            "grp:42"
        );
  }

  @Test
  void shouldGetSectorsOfTrafficPointSortedAndPaged() {
    SectorGroupVersion sectorGroupVersion = SectorTestData.getBasicSectorGroupVersion();
    sectorGroupVersion.setSloid("ch:1:sloid:group:777");
    sectorGroupVersion.setDesignation("D");
    sectorGroupVersionRepository.save(sectorGroupVersion);

    sectorGroupVersion.setSloid("ch:1:sloid:group:333");
    sectorGroupVersion.setDesignation("A");
    sectorGroupVersionRepository.save(sectorGroupVersion);

    Container<SectorGroupVersionModel> overview = sectorGroupService.getSectorGroupsOfTrafficPoint(
        sectorGroupVersion.getTrafficPointSloid(),
        PageRequest.of(0, 1, Sort.by("designation").ascending()));
    assertThat(overview.getTotalCount()).isEqualTo(1);
    assertThat(overview.getObjects().getFirst().getDesignation()).isEqualTo("A");
  }

  @Test
  void shouldGetSectorsBySectorGroupSloid() {
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
    List<SectorVersionModel> result = sectorGroupService.getSectorsBySectorGroupSloid(savedGroup.getSloid());

    // Then
    assertThat(result).hasSize(2)
        .extracting(SectorVersionModel::getSloid)
        .containsExactlyInAnyOrder(sector1, sector2);
    assertThat(result).extracting(SectorVersionModel::getDesignation).containsExactlyInAnyOrder("hehe", "dese");
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

    doReturn("ch:1:sloid:sector:1:0:1").when(locationService).generateSloid(SloidType.SECTOR_GROUP,
        toCreate.getTrafficPointSloid());

    ReadSectorGroupVersionModel result =
        sectorGroupService.createSectorGroup(toCreate, sloids, List.of(ServicePointTestData.getBern()));

    assertThat(sectorGroupVersionRepository.findById(result.getId())).isPresent();
    assertThat(result.getSloid()).isEqualTo("ch:1:sloid:sector:1:0:1");
    assertThat(result.getLength()).isEqualTo(36.0);
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
        sectorGroupService.createSectorGroup(toCreate, sloids, List.of())
    ).isInstanceOf(SectorNotExistingException.class);
  }

  @Test
  void shouldThrowWhenValidityIsNotInRangeOfTrafficPoint() {
    TrafficPointElementVersion trafficPointElementVersion = trafficPointElementVersionRepository.save(
        TrafficPointTestData.getBasicTrafficPoint());
    List<String> sloids = List.of("sector:A", "sector:B");

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("sector:A")
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2008, 1, 1))
        .designation("hehe")
        .build());

    sectorVersionRepository.save(SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid("sector:B")
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2008, 1, 1))
        .designation("huhu")
        .build());

    SectorGroupVersion toCreate = SectorTestData.getBasicSectorGroupVersion()
        .toBuilder()
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2005, 1, 1))
        .validTo(LocalDate.of(2009, 1, 1))
        .designation("hehe")
        .build();

    doReturn("ch:1:sloid:sector:1:0:2").when(locationService).generateSloid(SloidType.SECTOR_GROUP,
        toCreate.getTrafficPointSloid());

    assertThatThrownBy(() ->
        sectorGroupService.createSectorGroup(toCreate, sloids, List.of(ServicePointTestData.getBern()))
    ).isInstanceOf(SectorValidityException.class);
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
        sectorGroupService.createSectorGroup(toCreate, List.of(sloid, sloid2), List.of())
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
        sectorGroupService.createSectorGroup(toCreate, List.of("s1", "s2"), List.of())
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
