package ch.sbb.atlas.servicepointdirectory.service.loading.point;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LoadingPointImportServiceTest {

  @Autowired
  private LoadingPointImportService loadingPointImportService;

  @Autowired
  private LoadingPointRepository loadingPointRepository;

  @Test
  void shouldParseCsvSuccessfully() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DIDOK3_LADESTELLEN_20221214011257.csv");
    List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(csvStream);

    assertThat(loadingPointCsvModels).isNotEmpty();
  }

  @Test
  void shouldSaveParsedCsvToDb() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DIDOK3_LADESTELLEN_20221214011257.csv");
    List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(csvStream);

    loadingPointImportService.importLoadingPoints(loadingPointCsvModels);

    assertThat(loadingPointRepository.count()).isEqualTo(3018);
  }
}