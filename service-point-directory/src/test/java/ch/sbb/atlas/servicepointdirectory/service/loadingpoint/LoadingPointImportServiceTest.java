package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@WithMockJwtAuthentication
@ActiveProfiles("integration-test")
@Transactional
public class LoadingPointImportServiceTest {

  private static final String CSV_FILE = "DIDOK3_LADESTELLEN_20230803011047.csv";

  private final LoadingPointImportService loadingPointImportService;
  private final LoadingPointVersionRepository loadingPointVersionRepository;

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

}
