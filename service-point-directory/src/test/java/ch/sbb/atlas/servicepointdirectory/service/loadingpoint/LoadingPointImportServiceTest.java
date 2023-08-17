package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LoadingPointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_LADESTELLEN_20230803011047.csv";

  private final LoadingPointImportService loadingPointImportService;
  private final LoadingPointVersionRepository loadingPointVersionRepository;

  @MockBean
  private CrossValidationService crossValidationServiceMock;

  @Autowired
  public LoadingPointImportServiceTest(LoadingPointImportService loadingPointImportService,
      LoadingPointVersionRepository loadingPointVersionRepository) {
    this.loadingPointImportService = loadingPointImportService;
    this.loadingPointVersionRepository = loadingPointVersionRepository;
  }

  @Test
  void shouldParseCsvSuccessfully() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE)) {
      List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(csvStream);
      assertThat(loadingPointCsvModels).isNotEmpty();
      LoadingPointCsvModel csvModel = loadingPointCsvModels.get(0);
      assertThat(csvModel.getServicePointNumber()).isNotNull();
      assertThat(csvModel.getDesignation()).isNotNull();
      assertThat(csvModel.getCreatedAt()).isNotNull();
      assertThat(csvModel.getCreatedBy()).isNotNull();
    }
  }

  @Disabled("Is only for finding loading points in csv where multiple versions exist")
  @Test
  void findNumberOfLoadingPointVersionsInCsv() throws IOException {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE)) {
      List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(csvStream);

      Map<Integer, Map<Integer, Integer>> finalMap = new HashMap<>();
      Map<Integer, List<LoadingPointCsvModel>> didokCodeMap = loadingPointCsvModels.stream()
          .collect(Collectors.groupingBy(LoadingPointCsvModel::getServicePointNumber));
      didokCodeMap.forEach((didokCode, list) -> {
        final Map<Integer, List<LoadingPointCsvModel>> numberMap = list.stream()
            .collect(Collectors.groupingBy(LoadingPointCsvModel::getNumber));
        final Map<Integer, Integer> mapCountNumbers = new HashMap<>();
        numberMap.forEach((number, listOfModels) -> mapCountNumbers.put(number, listOfModels.size()));
        finalMap.put(didokCode, mapCountNumbers);
      });

      finalMap.forEach((didokCode, map) -> {
        boolean allMatch = map.values().stream().allMatch(number -> number == 1);
        if (!allMatch) {
          System.out.println(didokCode + "=" + map);
        }
      });

    }
  }

  @Test
  void shouldImportContainerWith2VersionsOnFirstRun() {
    // given
    doNothing().when(crossValidationServiceMock).validateServicePointNumberExists(any());
    final List<LoadingPointCsvModel> loadingPointCsvModels = List.of(
        LoadingPointCsvModel.builder()
            .designation("Ladestelle 1")
            .connectionPoint(false)
            .number(1)
            .servicePointNumber(85070001)
            .createdBy("fs111111")
            .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
            .editedBy("fs222222")
            .editedAt(LocalDateTime.of(2020, 1, 2, 1, 5))
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .build(),
        LoadingPointCsvModel.builder()
            .designation("Ladestelle 2")
            .connectionPoint(false)
            .number(1)
            .servicePointNumber(85070001)
            .createdBy("fs333333")
            .createdAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .editedBy("fs444444")
            .editedAt(LocalDateTime.of(2021, 1, 2, 1, 5))
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .build()
    );

    final List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers = List.of(
        LoadingPointCsvModelContainer.builder()
            .csvModelList(loadingPointCsvModels)
            .didokCode(85070001)
            .loadingPointNumber(1)
            .build()
    );

    // when
    final List<ItemImportResult> loadingPointItemImportResults = loadingPointImportService.importLoadingPoints(
        loadingPointCsvModelContainers);

    // then
    assertThat(loadingPointItemImportResults).hasSize(2);

    final List<LoadingPointVersion> dbVersions =
        loadingPointVersionRepository.findAllByServicePointNumberAndNumberOrderByValidFrom(
            ServicePointNumber.of(85070001), 1);

    assertThat(dbVersions).hasSize(2);
    assertThat(dbVersions.get(0).getId()).isNotNull();
    assertThat(dbVersions.get(0).getValidFrom()).isEqualTo("2020-01-01");

    assertThat(dbVersions.get(1).getId()).isNotNull();
    assertThat(dbVersions.get(1).getValidFrom()).isEqualTo("2022-01-01");
  }

  @Test
  void shouldImportContainerWithMergedVersionsOnSecondRun() {
    // given
    doNothing().when(crossValidationServiceMock).validateServicePointNumberExists(any());
    final List<LoadingPointCsvModel> loadingPointCsvModels = List.of(
        LoadingPointCsvModel.builder()
            .designation("Ladestelle 1")
            .connectionPoint(false)
            .number(1)
            .servicePointNumber(85070001)
            .createdBy("fs111111")
            .createdAt(LocalDateTime.of(2020, 1, 1, 1, 1))
            .editedBy("fs222222")
            .editedAt(LocalDateTime.of(2020, 1, 2, 1, 5))
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2020, 12, 31))
            .build(),
        LoadingPointCsvModel.builder()
            .designation("Ladestelle 2")
            .connectionPoint(false)
            .number(1)
            .servicePointNumber(85070001)
            .createdBy("fs333333")
            .createdAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .editedBy("fs444444")
            .editedAt(LocalDateTime.of(2021, 1, 2, 1, 5))
            .validFrom(LocalDate.of(2022, 1, 1))
            .validTo(LocalDate.of(2022, 12, 31))
            .build(),
        LoadingPointCsvModel.builder()
            .designation("Ladestelle 3")
            .connectionPoint(false)
            .number(1)
            .servicePointNumber(85070001)
            .createdBy("fs333333")
            .createdAt(LocalDateTime.of(2021, 1, 1, 1, 1))
            .editedBy("fs444444")
            .editedAt(LocalDateTime.of(2021, 1, 2, 1, 5))
            .validFrom(LocalDate.of(2023, 1, 1))
            .validTo(LocalDate.of(2023, 12, 31))
            .build()
    );

    final List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers = List.of(
        LoadingPointCsvModelContainer.builder()
            .csvModelList(loadingPointCsvModels)
            .didokCode(85070001)
            .loadingPointNumber(1)
            .build()
    );
    loadingPointImportService.importLoadingPoints(loadingPointCsvModelContainers);

    final List<LoadingPointCsvModel> loadingPointCsvModelsSecondRun = List.of(
        LoadingPointCsvModel.builder()
            .designation("Ladestelle 1")
            .connectionPoint(false)
            .number(1)
            .servicePointNumber(85070001)
            .createdBy("fs555555")
            .createdAt(LocalDateTime.of(2022, 1, 1, 1, 1))
            .editedBy("fs666666")
            .editedAt(LocalDateTime.of(2022, 1, 10, 10, 10))
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2023, 12, 31))
            .build()
    );

    final List<LoadingPointCsvModelContainer> loadingPointCsvModelContainersSecondRun = List.of(
        LoadingPointCsvModelContainer.builder()
            .csvModelList(loadingPointCsvModelsSecondRun)
            .didokCode(85070001)
            .loadingPointNumber(1)
            .build()
    );

    // when
    final List<ItemImportResult> loadingPointItemImportResults = loadingPointImportService.importLoadingPoints(
        loadingPointCsvModelContainersSecondRun);

    // then
    assertThat(loadingPointItemImportResults).hasSize(1);

    final List<LoadingPointVersion> dbVersions =
        loadingPointVersionRepository.findAllByServicePointNumberAndNumberOrderByValidFrom(ServicePointNumber.of(85070001), 1);

    assertThat(dbVersions).hasSize(1);
    assertThat(dbVersions.get(0).getId()).isNotNull();
    assertThat(dbVersions.get(0).getEditor()).isEqualTo("fs666666");
    assertThat(dbVersions.get(0).getCreator()).isEqualTo("fs555555");
    assertThat(dbVersions.get(0).getValidFrom()).isEqualTo("2020-01-01");
    assertThat(dbVersions.get(0).getValidTo()).isEqualTo("2023-12-31");
  }

}
