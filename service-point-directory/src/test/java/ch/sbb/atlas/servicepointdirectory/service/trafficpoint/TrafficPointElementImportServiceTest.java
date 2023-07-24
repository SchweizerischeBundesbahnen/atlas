package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public class TrafficPointElementImportServiceTest {

  @MockBean
  private TrafficPointElementValidationService trafficPointElementValidationService;

  private static final String CSV_FILE = "DIDOK3_VERKEHRSPUNKTELEMENTE_ALL_V_1_20221222011816.csv";

  private final TrafficPointElementImportService trafficPointElementImportService;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  public TrafficPointElementImportServiceTest(TrafficPointElementImportService trafficPointElementImportService,
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
  public void shouldImportTrafficPoints() {
    //given
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = List.of(
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:123")
            .trafficPointCsvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:123", 2))
            .build(),
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:567")
            .trafficPointCsvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:567", 2))
            .build()
    );
    //when
    List<TrafficPointItemImportResult> trafficPointItemImportResults = trafficPointElementImportService.importTrafficPoints(
        trafficPointCsvModelContainers);

    //then
    assertThat(trafficPointItemImportResults).hasSize(4);
    List<TrafficPointElementVersion> resultFirstContainer = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:123");
    assertThat(resultFirstContainer).hasSize(2);
    assertThat(resultFirstContainer.get(0).getId()).isNotNull();
    assertThat(resultFirstContainer.get(1).getId()).isNotNull();

    List<TrafficPointElementVersion> resultSecondContainer = trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
        "ch:1:sloid:567");
    assertThat(resultSecondContainer).hasSize(2);
    assertThat(resultSecondContainer.get(0).getId()).isNotNull();
    assertThat(resultSecondContainer.get(1).getId()).isNotNull();
  }

  @Test
  void shouldUpdateMergeOnSecondImportRun() {
    // given
    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers = List.of(
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:123")
            .trafficPointCsvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:123", 3))
            .build()
    );
    trafficPointElementImportService.importTrafficPoints(trafficPointCsvModelContainers);

    List<TrafficPointCsvModelContainer> trafficPointCsvModelContainersMerged = List.of(
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:123")
            .trafficPointCsvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:123", 2))
            .build()
    );
    trafficPointCsvModelContainersMerged.get(0).getTrafficPointCsvModelList().get(1).setValidTo(
        LocalDate.of(2022, 12, 31));
    trafficPointCsvModelContainersMerged.get(0).getTrafficPointCsvModelList().get(1).setHeight(1D);

    // when
    List<TrafficPointItemImportResult> trafficPointItemImportResults = trafficPointElementImportService.importTrafficPoints(
        trafficPointCsvModelContainersMerged);

    // then
    assertThat(trafficPointItemImportResults).hasSize(2);

    List<TrafficPointElementVersion> allBySloidOrderByValidFrom =
        trafficPointElementVersionRepository.findAllBySloidOrderByValidFrom(
            "ch:1:sloid:123");
    assertThat(allBySloidOrderByValidFrom).hasSize(2);
    assertThat(allBySloidOrderByValidFrom.get(0).getValidFrom()).isEqualTo(
        LocalDate.of(2020, 1, 1)
    );
    assertThat(allBySloidOrderByValidFrom.get(0).getValidTo()).isEqualTo(
        LocalDate.of(2020, 12, 31)
    );

    assertThat(allBySloidOrderByValidFrom.get(1).getValidFrom()).isEqualTo(
        LocalDate.of(2021, 1, 1)
    );
    assertThat(allBySloidOrderByValidFrom.get(1).getValidTo()).isEqualTo(
        LocalDate.of(2022, 12, 31)
    );
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
        "ch:1:sloid:123");
    assertThat(trafficPointElements).hasSize(2);
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getCreator()).isEqualTo("fs45117");
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getCreationDate())
        .isEqualTo(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)));
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getEditor()).isEqualTo("fs45117");
    assertThat(trafficPointElements.get(1).getTrafficPointElementGeolocation().getEditionDate())
        .isEqualTo(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)));
  }

  private List<TrafficPointElementCsvModel> getTrafficPointCsvModelVersions(String sloid, int numberOfVersions) {
    final int startingYear = 2020;
    final ArrayList<TrafficPointElementCsvModel> list = new ArrayList<>(List.of(
        TrafficPointElementCsvModel.builder()
            .sloid(sloid)
            .servicePointNumber(85700012)
            .validFrom(LocalDate.of(startingYear, 1, 1))
            .validTo(LocalDate.of(startingYear, 12, 31))
            .createdAt(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(5, 5)))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(10, 50)))
            .editedBy("fs11111")
            .build()
    ));

    for (double i = 0; i < numberOfVersions - 1; i++) {
      list.add(
          TrafficPointElementCsvModel.builder()
              .sloid(sloid)
              .nWgs84(47.5961061)
              .eWgs84(7.536484397)
              .height(i)
              .spatialReference(SpatialReference.WGS84)
              .servicePointNumber(85700012)
              .validFrom(LocalDate.of(startingYear + ((int) i + 1), 1, 1))
              .validTo(LocalDate.of(startingYear + ((int) i + 1), 12, 31))
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
