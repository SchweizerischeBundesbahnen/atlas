package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointElementCsvModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class TrafficPointElementImportServiceTest {

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
      assertThat(csvModel.getTrafficPointElementType()).isNotNull();
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
            .trafficPointCsvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:123"))
            .build(),
        TrafficPointCsvModelContainer.builder()
            .sloid("ch:1:sloid:567")
            .trafficPointCsvModelList(getTrafficPointCsvModelVersions("ch:1:sloid:567"))
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

  private List<TrafficPointElementCsvModel> getTrafficPointCsvModelVersions(String sloid) {
    return List.of(
        TrafficPointElementCsvModel.builder()
            .sloid(sloid)
            .servicePointNumber(85700012)
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2022, 1, 1))
            .createdAt(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(5, 5)))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(10, 50)))
            .editedBy("fs11111")
            .build(),
        TrafficPointElementCsvModel.builder()
            .sloid(sloid)
            .nWgs84(47.5961061)
            .eWgs84(7.536484397)
            .spatialReference(SpatialReference.WGS84)
            .servicePointNumber(85700012)
            .validFrom(LocalDate.of(2022, 1, 2))
            .validTo(LocalDate.of(2022, 12, 31))
            .createdAt(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(5, 5)))
            .createdBy("fs11111")
            .editedAt(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(10, 50)))
            .editedBy("fs11111")
            .build()
    );
  }
}
