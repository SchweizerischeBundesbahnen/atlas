package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.client.location.LocationClientV1;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TrafficPointElementImportServiceTest {

  // required for test functionality
  @MockBean
  private CrossValidationService crossValidationService;

  @MockBean
  private LocationClientV1 locationClient;

  private static final String CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221222011816.csv";

  private final TrafficPointElementImportService trafficPointElementImportService;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  TrafficPointElementImportServiceTest(TrafficPointElementImportService trafficPointElementImportService,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.trafficPointElementImportService = trafficPointElementImportService;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @Test
  void shouldParseCsvSuccessfully() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE)) {
      List<TrafficPointElementCsvModel> trafficPointElementCsvModels = TrafficPointElementImportService.parseTrafficPointElements(
          csvStream);

      TrafficPointElementCsvModel csvModel = trafficPointElementCsvModels.get(0);
      assertThat(csvModel.getTrafficPointElementType()).isEqualTo(0);
      assertThat(csvModel.getCreatedAt()).isNotNull();
      assertThat(csvModel.getCreatedBy()).isNotNull();
      assertThat(trafficPointElementCsvModels).isNotEmpty();
    }
  }

  @Test
  void shouldImportTrafficPoints() {
    //given
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = List.of(
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:700012:123:123")
            .csvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:700012:123:123", 2020, 1, 2, 1))
            .build(),
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:700012:432:422")
            .csvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:700012:432:422", 2020, 1, 2, 1))
            .build()
    );
    //when
    List<ItemImportResult> trafficPointItemImportResults = trafficPointElementImportService.importTrafficPoints(
        trafficPointCsvModelContainers);

    //then
    verify(locationClient, times(1)).claimSloid(
        argThat(claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PLATFORM && Objects.equals(
            claimSloidRequestModel.sloid(), "ch:1:sloid:700012:123:123")));
    verify(locationClient, times(1)).claimSloid(
        argThat(claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PLATFORM && Objects.equals(
            claimSloidRequestModel.sloid(), "ch:1:sloid:700012:432:422")));

    assertThat(trafficPointItemImportResults).hasSize(4);
    List<TrafficPointElementVersion> resultFirstContainer = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:700012:123:123");
    assertThat(resultFirstContainer).hasSize(2);
    assertThat(resultFirstContainer.get(0).getId()).isNotNull();
    assertThat(resultFirstContainer.get(1).getId()).isNotNull();

    List<TrafficPointElementVersion> resultSecondContainer = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:700012:432:422");
    assertThat(resultSecondContainer).hasSize(2);
    assertThat(resultSecondContainer.get(0).getId()).isNotNull();
    assertThat(resultSecondContainer.get(1).getId()).isNotNull();
  }

  @Test
  void shouldUpdateMergeOnSecondImportRun() {
    // given
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = List.of(
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:700012:123:123")
            .csvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:700012:123:123", 2020, 1, 6, 1))
            .build()
    );
    trafficPointElementImportService.importTrafficPoints(trafficPointCsvModelContainers);

    List<TrafficPointElementCsvModel> trafficPointCsvModelVersionsMerged =
        getTrafficPointCsvModelVersions("ch:1:sloid:700012:123:123", 2020, 2, 1, 1);
    trafficPointCsvModelVersionsMerged.addAll(
        getTrafficPointCsvModelVersions("ch:1:sloid:700012:123:123", 2022, 1, 2, 2)
    );
    trafficPointCsvModelVersionsMerged.addAll(
        getTrafficPointCsvModelVersions("ch:1:sloid:700012:123:123", 2024, 2, 1, 4)
    );

    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainersMerged = List.of(
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:700012:123:123")
            .csvModelList(trafficPointCsvModelVersionsMerged)
            .build()
    );

    // when
    List<ItemImportResult> trafficPointItemImportResults = trafficPointElementImportService.importTrafficPoints(
        trafficPointCsvModelContainersMerged);

    // then
    verify(locationClient, times(1)).claimSloid(
        argThat(claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PLATFORM && Objects.equals(
            claimSloidRequestModel.sloid(), "ch:1:sloid:700012:123:123")));

    assertThat(trafficPointItemImportResults).hasSize(4);
    List<TrafficPointElementVersion> allBySloidOrderByValidFrom =
        trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
            "ch:1:sloid:700012:123:123");
    assertThat(allBySloidOrderByValidFrom).hasSize(4);
    assertThat(allBySloidOrderByValidFrom.get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2020, 1, 1)
    );
    assertThat(allBySloidOrderByValidFrom.get(0).getValidTo()).isEqualTo(
        LocalDate.of(2021, 12, 31)
    );

    assertThat(allBySloidOrderByValidFrom.get(1).getValidFrom()).isEqualTo(
        LocalDate.of(2022, 1, 1)
    );
    assertThat(allBySloidOrderByValidFrom.get(1).getValidTo()).isEqualTo(
        LocalDate.of(2022, 12, 31)
    );

    assertThat(allBySloidOrderByValidFrom.get(2).getValidFrom()).isEqualTo(
        LocalDate.of(2023, 1, 1)
    );
    assertThat(allBySloidOrderByValidFrom.get(2).getValidTo()).isEqualTo(
        LocalDate.of(2023, 12, 31)
    );

    assertThat(allBySloidOrderByValidFrom.get(3).getValidFrom()).isEqualTo(
        LocalDate.of(2024, 1, 1)
    );
    assertThat(allBySloidOrderByValidFrom.get(3).getValidTo()).isEqualTo(
        LocalDate.of(2025, 12, 31)
    );

    assertThat(allBySloidOrderByValidFrom.get(0).getTrafficPointElementGeolocation().getHeight()).isEqualTo(1);
    assertThat(allBySloidOrderByValidFrom.get(1).getTrafficPointElementGeolocation().getHeight()).isEqualTo(2);
    assertThat(allBySloidOrderByValidFrom.get(2).getTrafficPointElementGeolocation().getHeight()).isEqualTo(3);
    assertThat(allBySloidOrderByValidFrom.get(3).getTrafficPointElementGeolocation().getHeight()).isEqualTo(4);
  }

  @Test
  void shouldUpdateTrafficPointElementVersionImport_withImportVersioning_andSetParentPropertiesOnGeolocation() {
    // given
    TrafficPointElementVersion savedVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementVersionRepository.save(savedVersion);

    TrafficPointElementVersion editedVersion = TrafficPointTestData.getBasicTrafficPoint();
    editedVersion.setValidFrom(LocalDate.of(2024, 1, 2));
    editedVersion.setValidTo(LocalDate.of(2024, 12, 31));
    editedVersion.getTrafficPointElementGeolocation().setHeight(100D);

    editedVersion.getTrafficPointElementGeolocation().setCreator("test");
    editedVersion.getTrafficPointElementGeolocation().setCreationDate(
        LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.of(1, 1, 1))
    );
    editedVersion.getTrafficPointElementGeolocation().setEditor("test2");
    editedVersion.getTrafficPointElementGeolocation().setEditionDate(
        LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.of(1, 1, 1))
    );

    // when
    trafficPointElementImportService.updateTrafficPointElementVersionImport(editedVersion);

    // then
    List<TrafficPointElementVersion> trafficPointElements = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:89108:123:123");
    assertThat(trafficPointElements).hasSize(2);
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getCreator()).isEqualTo("fs45117");
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getCreationDate())
        .isEqualTo(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)));
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getEditor()).isEqualTo("fs45117");
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getEditionDate())
        .isEqualTo(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)));
  }

  @Test
  void shouldUpdateValidToAndEditionPropertiesCorrectlyOnSecondRun() {
    // given
    final List<TrafficPointElementCsvModel> trafficPointCsvModels = List.of(
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:700012:123:123")
            .servicePointNumber(85700012)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2023, 12, 31))
            .createdAt(LocalDateTime.of(2020, 1, 15, 5, 5))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(2020, 1, 15, 5, 5))
            .editedBy("fs11111")
            .build()
    );

    final List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = List.of(
        TrafficPointCsvModelContainer.builder()
            .csvModelList(trafficPointCsvModels)
            .sloid("ch:1:sloid:700012:123:123")
            .build()
    );
    trafficPointElementImportService.importTrafficPoints(trafficPointCsvModelContainers);

    final List<TrafficPointElementCsvModel> trafficPointCsvModelsSecondRun = List.of(
        TrafficPointElementCsvModel.builder()
            .sloid("ch:1:sloid:700012:123:123")
            .servicePointNumber(85700012)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2021, 6, 15))
            .createdAt(LocalDateTime.of(2020, 1, 15, 5, 5))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(2023, 1, 15, 5, 5))
            .editedBy("fs22222")
            .build()
    );

    final List<TrafficPointCsvModelContainer> trafficPointCsvModelContainersSecondRun = List.of(
        TrafficPointCsvModelContainer.builder()
            .csvModelList(trafficPointCsvModelsSecondRun)
            .sloid("ch:1:sloid:700012:123:123")
            .build()
    );

    // when
    final List<ItemImportResult> trafficPointItemImportResults = trafficPointElementImportService.importTrafficPoints(
        trafficPointCsvModelContainersSecondRun);

    // then
    verify(locationClient, times(1)).claimSloid(
        argThat(claimSloidRequestModel -> claimSloidRequestModel.sloidType() == SloidType.PLATFORM && Objects.equals(
            claimSloidRequestModel.sloid(), "ch:1:sloid:700012:123:123")));

    assertThat(trafficPointItemImportResults).hasSize(1);

    final List<TrafficPointElementVersion> dbVersions =
        trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom("ch:1:sloid:700012:123:123");

    assertThat(dbVersions).hasSize(1);
    assertThat(dbVersions.get(0).getEditor()).isEqualTo("fs22222");
    assertThat(dbVersions.get(0).getEditionDate()).isEqualTo(LocalDateTime.of(2023, 1, 15, 5, 5));
    assertThat(dbVersions.get(0).getCreator()).isEqualTo("fs11111");
    assertThat(dbVersions.get(0).getCreationDate()).isEqualTo(LocalDateTime.of(2020, 1, 15, 5, 5));
    assertThat(dbVersions.get(0).getValidFrom()).isEqualTo("2020-01-01");
    assertThat(dbVersions.get(0).getValidTo()).isEqualTo("2021-06-15");
  }

  private List<TrafficPointElementCsvModel> getTrafficPointCsvModelVersions(String sloid,
      int startingYear, int yearsPerVersion, int numberOfVersions, double startingHeight) {
    final ArrayList<TrafficPointElementCsvModel> list = new ArrayList<>();
    for (int i = 0; i < numberOfVersions; i++, startingYear += yearsPerVersion, startingHeight += 1) {
      list.add(
          TrafficPointElementCsvModel.builder()
              .sloid(sloid)
              .nWgs84(47.5961061)
              .eWgs84(7.536484397)
              .height(startingHeight)
              .spatialReference(SpatialReference.WGS84)
              .servicePointNumber(85700012)
              .validFrom(LocalDate.of(startingYear, 1, 1))
              .validTo(LocalDate.of(startingYear + yearsPerVersion - 1, 12, 31))
              .createdAt(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(5, 5)))
              .createdBy("fs11111")
              .editedAt(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(10, 50)))
              .editedBy("fs11111")
              .build()
      );
    }
    return list;
  }
}
