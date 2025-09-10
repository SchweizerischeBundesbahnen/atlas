package ch.sbb.atlas.servicepointdirectory.module.sector.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.sector.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.MissingTrainStopPointException;
import ch.sbb.atlas.servicepointdirectory.module.sector.exception.SectorValidityException;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.repository.TrafficPointElementVersionRepository;
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
class SectorServiceTest {

  @MockitoBean
  private LocationService locationService;

  private final SectorService sectorService;
  private final SectorVersionRepository sectorVersionRepository;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  public SectorServiceTest(SectorService sectorService, SectorVersionRepository sectorVersionRepository,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.sectorService = sectorService;
    this.sectorVersionRepository = sectorVersionRepository;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @AfterEach
  void cleanup() {
    sectorVersionRepository.deleteAll();
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  @Transactional
  void shouldMergeSector() {
    //Given
    trafficPointElementVersionRepository.save(TrafficPointTestData.getBasicTrafficPoint());

    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();
    sectorVersion = sectorVersionRepository.save(sectorVersion);

    SectorVersion edited = SectorTestData.getBasicSectorVersion();
    edited.setValidFrom(LocalDate.of(2022, 1, 2));
    edited.setValidTo(LocalDate.of(2024, 1, 1));
    edited.setVersion(sectorVersion.getVersion());

    // when
    sectorService.updateSector(sectorVersion, edited);

    // then
    assertThat(sectorService.getSector("ch:1:sloid:sector:1")).hasSize(1);
  }

  @Test
  void shouldGetSectorsOfTrafficPointSortedAndPaged() {
    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();
    sectorVersion.setSloid("ch:1:sloid:sector:1");
    sectorVersion.setDesignation("D");
    sectorVersionRepository.save(sectorVersion);

    sectorVersion.setSloid("ch:1:sloid:sector:2");
    sectorVersion.setDesignation("A");
    sectorVersionRepository.save(sectorVersion);

    Container<SectorVersionModel> overview = sectorService.getSectorsOfTrafficPoint(sectorVersion.getTrafficPointSloid(),
        PageRequest.of(0, 1, Sort.by("designation").ascending()));
    assertThat(overview.getTotalCount()).isEqualTo(1);
    assertThat(overview.getObjects().getFirst().getDesignation()).isEqualTo("A");
  }

  @Test
  void shouldThrowWhenTrafficPointSloidNotExists() {
    SectorVersion sectorVersion = SectorTestData.getBasicSectorVersion();
    sectorVersion.setSloid("ch:1:sloid:sector:1");
    sectorVersion.setDesignation("D");
    sectorVersionRepository.save(sectorVersion);

    sectorVersion.setSloid("ch:1:sloid:sector:2");
    sectorVersion.setDesignation("A");
    sectorVersionRepository.save(sectorVersion);

    assertThatThrownBy(
        () -> sectorService.getSectorsOfTrafficPoint("abc", PageRequest.of(0, 1, Sort.by("designation").ascending())))
        .isInstanceOf(SloidNotFoundException.class);
  }

  @Test
  void shouldGetSectorVersionByIdWhenExists() {
    // Given
    SectorVersion saved = sectorVersionRepository.save(SectorTestData.getBasicSectorVersion());
    // When
    SectorVersion fetched = sectorService.getSectorVersionById(saved.getId());
    // Then
    assertThat(fetched.getId()).isEqualTo(saved.getId());
    assertThat(fetched.getSloid()).isEqualTo(saved.getSloid());
  }

  @Test
  void shouldThrowWhenGetSectorVersionByIdNotFound() {
    // Given
    Long nonexistentId = 999L;
    // When
    // Then
    assertThatThrownBy(() -> sectorService.getSectorVersionById(nonexistentId))
        .isInstanceOf(IdNotFoundException.class);
  }

  @Test
  void shouldCreateSector() {
    //Given
    TrafficPointElementVersion trafficPointElementVersion =
        trafficPointElementVersionRepository.save(TrafficPointTestData.getBasicTrafficPoint());

    SectorVersion sectorVersionModel = SectorVersion.builder()
        .trafficPointSloid(trafficPointElementVersion.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("test")
        .length(18.00)
        .north(1111.111)
        .east(222.222)
        .spatialReference(SpatialReference.LV95)
        .height(19.0)
        .edgeHeight(20.0)
        .build();

    doReturn("ch:1:sloid:sector:1:0:1").when(locationService).generateSloid(SloidType.SECTOR,
        sectorVersionModel.getTrafficPointSloid());

    //when
    SectorVersion saved = sectorService.createSector(sectorVersionModel, List.of(ServicePointTestData.getBern()));

    //Then
    assertThat(sectorVersionRepository.findById(saved.getId())).isNotNull();
    assertThat(saved.getSloid()).isEqualTo("ch:1:sloid:sector:1:0:1");
  }

  @Test
  void shouldThrowWhenCreateSectorWithoutTrafficPoint() {
    // Given
    SectorVersion model = SectorVersion.builder()
        .trafficPointSloid("nonexistent-sloid")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("foo")
        .length(1.0).north(0.0).east(0.0)
        .spatialReference(SpatialReference.LV95)
        .height(1.0).edgeHeight(1.0)
        .build();

    // When
    // Then
    assertThatThrownBy(() -> sectorService.createSector(model, List.of(ServicePointTestData.getBern())))
        .isInstanceOf(SloidNotFoundException.class);
  }

  @Test
  void shouldThrowWhenServicePointIsNotStopPointAndIsMissingTrainAsMeansOfTransport() {
    // Given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();

    trafficPointElementVersion.setServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(8519768));

    TrafficPointElementVersion saved =
        trafficPointElementVersionRepository.save(trafficPointElementVersion);
    SectorVersion model = SectorVersion.builder()
        .trafficPointSloid(saved.getSloid())
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2024, 1, 1))
        .designation("foo")
        .length(1.0).north(0.0).east(0.0)
        .spatialReference(SpatialReference.LV95)
        .height(1.0).edgeHeight(1.0)
        .build();

    // When
    // Then
    assertThatThrownBy(() -> sectorService.createSector(model, List.of(ServicePointTestData.createServicePointVersion())))
        .isInstanceOf(MissingTrainStopPointException.class);
  }

  @Test
  void shouldThrowWhenValidityOfSectorIsNotInRangeOfValidityTrafficPoint() {
    // Given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();

    TrafficPointElementVersion saved =
        trafficPointElementVersionRepository.save(trafficPointElementVersion);

    SectorVersion model = SectorVersion.builder()
        .trafficPointSloid(saved.getSloid())
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2044, 1, 1))
        .designation("foo")
        .length(1.0).north(0.0).east(0.0)
        .spatialReference(SpatialReference.LV95)
        .height(1.0).edgeHeight(1.0)
        .build();

    // When
    // Then
    assertThatThrownBy(() -> sectorService.createSector(model, List.of(ServicePointTestData.createServicePointVersion())))
        .isInstanceOf(SectorValidityException.class);
  }

  @Test
  @Transactional
  void shouldThrowWhenValidityOfSectorIsNotInRangeOfValidityTrafficPointOnUpdate() {

    trafficPointElementVersionRepository.save(TrafficPointTestData.getBasicTrafficPoint());

    // Given
    String sloid = "ch:1:sector:update";
    SectorVersion original = sectorVersionRepository.save(
        SectorTestData.getBasicSectorVersion().toBuilder()
            .sloid(sloid)
            .version(1)
            .designation("orig")
            .build()
    );

    SectorVersion edited = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(original.getSloid())
        .trafficPointSloid(original.getTrafficPointSloid())
        .version(original.getVersion())
        .validFrom(original.getValidFrom().plusDays(2))
        .validTo(LocalDate.of(9999, 1, 1))
        .designation("updated")
        .length(original.getLength())
        .north(original.getNorth())
        .east(original.getEast())
        .spatialReference(original.getSpatialReference())
        .height(original.getHeight())
        .edgeHeight(original.getEdgeHeight())
        .build();

    // When
    // Then
    assertThatThrownBy(() -> sectorService.updateSector(original, edited))
        .isInstanceOf(SectorValidityException.class);
  }

  @Test
  void shouldFindAllVersionsAndGetSectorModels() {
    String sloid = "ch:1:sector:multi";

    SectorVersion v1 = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sloid)
        .validFrom(LocalDate.of(2022, 1, 1))
        .designation("v1")
        .build();

    SectorVersion v2 = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sloid)
        .validFrom(LocalDate.of(2023, 1, 1))
        .designation("v2")
        .build();
    sectorVersionRepository.saveAll(List.of(v2, v1));

    // When
    List<SectorVersion> entities = sectorService.getSector(sloid);
    // Then
    assertThat(entities).extracting(SectorVersion::getDesignation)
        .containsExactly("v1", "v2");
  }

  @Test
  void shouldThrowWhenSloidNotFound() {
    String sloid = "ch:1:sector:multi";

    SectorVersion v1 = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sloid)
        .validFrom(LocalDate.of(2022, 1, 1))
        .designation("v1")
        .build();

    SectorVersion v2 = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(sloid)
        .validFrom(LocalDate.of(2023, 1, 1))
        .designation("v2")
        .build();
    sectorVersionRepository.saveAll(List.of(v2, v1));

    // When
    // Then
    assertThatThrownBy(() -> sectorService.getSector("abc"))
        .isInstanceOf(SloidNotFoundException.class);
  }

  @Test
  @Transactional
  void shouldUpdateSectorAndCreateNewVersion() {
    trafficPointElementVersionRepository.save(TrafficPointTestData.getBasicTrafficPoint());

    String sloid = "ch:1:sector:update";
    SectorVersion original = sectorVersionRepository.save(
        SectorTestData.getBasicSectorVersion().toBuilder()
            .sloid(sloid)
            .version(1)
            .designation("orig")
            .build()
    );

    SectorVersion edited = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(original.getSloid())
        .trafficPointSloid(original.getTrafficPointSloid())
        .version(original.getVersion())
        .validFrom(original.getValidFrom().plusDays(2))
        .validTo(original.getValidTo().plusYears(1))
        .designation("updated")
        .length(original.getLength())
        .north(original.getNorth())
        .east(original.getEast())
        .spatialReference(original.getSpatialReference())
        .height(original.getHeight())
        .edgeHeight(original.getEdgeHeight())
        .build();

    // When
    sectorService.updateSector(original, edited);
    // Then
    List<SectorVersion> all = sectorVersionRepository.findAllBySloidOrderByValidFrom(sloid);
    assertThat(all).hasSize(2);

    SectorVersion latest = all.get(1);
    assertThat(latest.getDesignation()).isEqualTo("updated");
    assertThat(latest.getVersion()).isGreaterThan(original.getVersion());
  }

  @Test
  @Transactional
  void shouldThrowWhenUpdateSectorWithStaleVersion() {
    // Given
    SectorVersion original = sectorVersionRepository.save(SectorTestData.getBasicSectorVersion());

    SectorVersion edited = SectorTestData.getBasicSectorVersion().toBuilder()
        .sloid(original.getSloid())
        .trafficPointSloid(original.getTrafficPointSloid())
        .version(original.getVersion() + 1)
        .designation("hehe")
        .validFrom(original.getValidFrom())
        .validTo(original.getValidTo())
        .length(original.getLength())
        .north(original.getNorth())
        .east(original.getEast())
        .spatialReference(original.getSpatialReference())
        .height(original.getHeight())
        .edgeHeight(original.getEdgeHeight())
        .build();

    // When
    edited.setVersion(original.getVersion() + 1);

    // Then
    assertThatThrownBy(() -> sectorService.updateSector(original, edited))
        .isInstanceOf(StaleObjectStateException.class);
  }
}
