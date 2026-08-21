package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.module.sector.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.user.administration.security.service.CountryAndBusinessOrganisationBasedUserAdministrationService;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class SectorBulkImportServiceTest {

  @MockitoBean
  private CountryAndBusinessOrganisationBasedUserAdministrationService administrationService;

  @MockitoBean
  private LocationService locationService;

  @Autowired
  private SectorBulkImportService sectorBulkImportService;

  @Autowired
  private SectorVersionRepository sectorVersionRepository;

  @Autowired
  private TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  private ServicePointVersionRepository servicePointVersionRepository;

  private TrafficPointElementVersion bernWylereggPlatform;

  @BeforeEach
  void setUp() {
    doReturn(true).when(administrationService).hasUserPermissionsToCreateOrEditServicePointDependentObject(any(), any());
    ServicePointVersion stopPointWithTrain = ServicePointTestData.getBernWyleregg();
    stopPointWithTrain.setMeansOfTransport(Set.of(MeanOfTransport.TRAIN));
    servicePointVersionRepository.save(stopPointWithTrain);

    bernWylereggPlatform = trafficPointElementVersionRepository.save(TrafficPointTestData.getWylerEggPlatform());
  }

  @AfterEach
  void tearDown() {
    sectorVersionRepository.deleteAll();
    trafficPointElementVersionRepository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldCreateSectorGeneratingSloid() {
    String trafficPointSloid = bernWylereggPlatform.getSloid();
    String generatedSloid = "ch:1:sloid:89008:0:123:1";
    when(locationService.generateSloid(SloidType.SECTOR, trafficPointSloid)).thenReturn(generatedSloid);

    sectorBulkImportService.createSector(BulkImportUpdateContainer.<SectorCreateCsvModel>builder()
        .object(SectorCreateCsvModel.builder()
            .trafficPointSloid(trafficPointSloid)
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .designation("A")
            .length(12.0)
            .edgeHeight(11.0)
            .east(2600037.945)
            .north(1199749.812)
            .spatialReference(SpatialReference.LV95)
            .height(540.2)
            .build())
        .build());

    SectorVersion sectorVersion = sectorVersionRepository.findAllBySloidOrderByValidFrom(
        generatedSloid).getFirst();
    assertThat(sectorVersion.getSloid()).isNotNull().isEqualTo(generatedSloid);
  }

  @Test
  void shouldCreateSectorInNameOf() {
    String trafficPointSloid = bernWylereggPlatform.getSloid();
    String generatedSloid = "ch:1:sloid:89008:0:123:1";
    when(locationService.generateSloid(SloidType.SECTOR, trafficPointSloid)).thenReturn(generatedSloid);

    String creator = "e123456";
    sectorBulkImportService.createSectorByUserName(creator, BulkImportUpdateContainer.<SectorCreateCsvModel>builder()
        .object(SectorCreateCsvModel.builder()
            .trafficPointSloid(trafficPointSloid)
            .validFrom(bernWylereggPlatform.getValidFrom())
            .validTo(bernWylereggPlatform.getValidTo())
            .designation("A")
            .length(12.0)
            .edgeHeight(11.0)
            .east(2600037.945)
            .north(1199749.812)
            .spatialReference(SpatialReference.LV95)
            .height(540.2)
            .build())
        .build());

    SectorVersion sectorVersion = sectorVersionRepository.findAllBySloidOrderByValidFrom(
        generatedSloid).getFirst();
    assertThat(sectorVersion.getSloid()).isNotNull().isEqualTo(generatedSloid);
    assertThat(sectorVersion.getCreator()).isEqualTo(creator);
  }

  @Test
  void shouldUpdateSector() {
    SectorVersion existingSector = givenExistingSector();

    sectorBulkImportService.updateSector(BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
        .object(SectorUpdateCsvModel.builder()
            .sloid(existingSector.getSloid())
            .validFrom(existingSector.getValidFrom())
            .validTo(existingSector.getValidTo())
            .designation("B")
            .length(20.0)
            .build())
        .build());

    SectorVersion updatedSector = sectorVersionRepository.findAllBySloidOrderByValidFrom(
        existingSector.getSloid()).getFirst();
    assertThat(updatedSector.getDesignation()).isEqualTo("B");
    assertThat(updatedSector.getLength()).isEqualTo(20.0);
    assertThat(updatedSector.getEdgeHeight()).isEqualTo(16.0);
  }

  @Test
  void shouldUpdateSectorInNameOf() {
    SectorVersion existingSector = givenExistingSector();

    String editor = "e123456";
    sectorBulkImportService.updateSectorByUserName(editor, BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
        .object(SectorUpdateCsvModel.builder()
            .sloid(existingSector.getSloid())
            .validFrom(existingSector.getValidFrom())
            .validTo(existingSector.getValidTo())
            .designation("C")
            .build())
        .build());

    SectorVersion updatedSector = sectorVersionRepository.findAllBySloidOrderByValidFrom(
        existingSector.getSloid()).getFirst();
    assertThat(updatedSector.getDesignation()).isEqualTo("C");
    assertThat(updatedSector.getEditor()).isEqualTo(editor);
  }

  @Test
  void shouldThrowWhenSectorToUpdateDoesNotExist() {
    BulkImportUpdateContainer<SectorUpdateCsvModel> container =
        BulkImportUpdateContainer.<SectorUpdateCsvModel>builder()
            .object(SectorUpdateCsvModel.builder()
                .sloid("ch:1:sloid:89008:0:123:404")
                .validFrom(LocalDate.of(2022, 1, 1))
                .validTo(LocalDate.of(2024, 1, 1))
                .build())
            .build();

    assertThatExceptionOfType(SloidNotFoundException.class)
        .isThrownBy(() -> sectorBulkImportService.updateSector(container));
  }

  private SectorVersion givenExistingSector() {
    return sectorVersionRepository.saveAndFlush(SectorVersion.builder()
        .sloid("ch:1:sloid:89008:0:123:1")
        .trafficPointSloid(bernWylereggPlatform.getSloid())
        .validFrom(bernWylereggPlatform.getValidFrom())
        .validTo(bernWylereggPlatform.getValidTo())
        .designation("A")
        .length(17.5)
        .edgeHeight(16.0)
        .east(2600037.945)
        .north(1199749.812)
        .spatialReference(SpatialReference.LV95)
        .height(540.2)
        .status(Status.VALIDATED)
        .build());
  }

}